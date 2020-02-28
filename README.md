

# 项目模块
* 1 client 客户端   
* 2 server 服务   
* 3 proto 协议   
* 4 codec 编解码  
* 5 transport 数据传输  
* 6 common 工具  
* 7 example 测试样例  


## 1.Proto 模块
> Proto模块用于规定数据传输协议和规约，其主要类有3个：
>> 1.1 Request类用于储存某一需要执行方法的method描述（即serviceDescriptor）与实参。
```
@Data
public class Request {
    private ServiceDescriptor serviceDescriptor;
    private Object [] parameters;
}
```
>>这里的实参将会在client调用方法时通过动态代理获取，并且通过http协议传递到Server进行处理。而在Server中会根据传递的class与实例通过反射进行实际方法的执行，最后将执行结果通过Response类进行返回。  
>> 1.2 Reponse类描述如下：
```
@Data
public class Response {
    /**
     * 服务返回编码，0-成功 ，非0失败
     */
    private int code;//成功与否
    /**
     * 具体的错误信息
     */
    private String message  = "ok";
    /**
     * 返回的数据
     */
    private Object data;
}

```
>> 1.3 ServiceDescriptor类描述如下：
```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor {
    private String clazz;//类名
    private String method;//方法名
    private String returnType;//返回类型
    private String[] parameterTypes;//参数类型

    public static ServiceDescriptor from(Class clazz, Method method){
        ServiceDescriptor sdp = new ServiceDescriptor();
        sdp.setClazz(clazz.getName());
        sdp.setMethod(method.getName());
        sdp.setReturnType(method.getReturnType().getName());

        Class[] parameterClasses =  method.getParameterTypes();
        String[] parameterTypes = new String[parameterClasses.length];
        for(int i =0;i<parameterClasses.length;i++){
            parameterTypes[i] = parameterClasses[i].getName();
        }
        sdp.setParameterTypes(parameterTypes);

        return sdp;
    }
    // ...省略hashcode equals toString
}
```
>> 该类主要用于存储方法信息，而在Server中会将类映射到对应于该类的具体实例，以便反射执行具体方法。

## 2 Transport 模块
> 该模块主要用于client与server的http通信处理问题，其client请求内容以Request类形式封装传输，server响应内容以Reponse类封装返回。
>> 2.1 HTTPTransportClient类实现如下：
```
public class HTTPTransportClient implements  TransportClient{
    private String url;
    @Override
    public void connect(Peer peer) {
        this.url="http://"+peer.getHost()+":"+peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {

        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            httpConn.connect();
            IOUtils.copy(data,httpConn.getOutputStream());

            int resultCode = httpConn.getResponseCode();
            if(resultCode==HttpURLConnection.HTTP_OK){
                return httpConn.getInputStream();
            }else{
                return httpConn.getErrorStream();
            }
        } catch (IOException e) {
            throw  new IllegalStateException(e);
        }

    }
    @Override
    public void close() {}
}

```
>> Peer类为定义在Proto模块中的Server通信地址与端口不做特殊介绍。该类主要方法为write(),主要是用于向server传递数据并且获取响应数据。其最终调用将在RpcClient类中调用。  
> 2.2 HTTPTransportServer 类主要实现如下：
```
@Slf4j
public class HttpTransportServer implements TransportServer {

    private RequestHandler handler;
    private Server server;

    @Override
    public void init(int port, RequestHandler handler) {
        this.handler = handler;
        this.server = new Server(port);

        //servlet 接收请求
        ServletContextHandler ctx = new ServletContextHandler();
        server.setHandler(ctx);

        ServletHolder holder = new ServletHolder(new RequestServlet());
        ctx.addServlet(holder,"/*");
    }

    @Override
    public void start() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    class RequestServlet extends HttpServlet{
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            log.info("client connect");

            InputStream in =req.getInputStream();
            OutputStream out = resp.getOutputStream();

            if(handler !=null){
                handler.onRequest(in,out);
            }
            out.flush();
        }
    }
}

```
>> 上述类使用Jetty容器完成init(),start(),stop()功能。上述类最重要一个关注点在于RequestHandler实例的初始化，该抽象类定义于Transport模块，主要用于server处理来自client的请求。其抽象方法实现将在RpcServer类中详细讲解。  
## 3 Common 模块
> common模块主要为一些反射工具，其具体实现如下:
```
public class ReflectionUtils {
    /**
     * 根据class创建对象
     *
     * @param clazz 待创建兑现骨干的类
     * @param <T>   对象类型
     * @return 创建好的对象
     */
    public static <T> T newInstance(Class<T> clazz) throws IllegalStateException{

        try {
            return clazz.newInstance();
        } catch (Exception e) {
           throw new IllegalStateException(e);
        }
    }

    /**
     * 获取某个class的共有方法
     *
     * @param clazz
     * @return 当前类声明的共有方法
     */
    public static Method[] getPublicMethods(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> pmethods = new ArrayList<>();
        for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers())) {
                pmethods.add(m);
            }
        }
        return pmethods.toArray(new Method[0]);
    }

    /**
     * 调用指定对象的方法
     *
     * @param obj    被调用的对象
     * @param method 被调用的方法
     * @param args   方法的参数
     * @return 返回结果
     */
    public static Object invoke(Object obj, Method method, Object... args) throws IllegalStateException{
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
           throw new IllegalStateException(e);
        }
    }

}
```
>> 上述getPublicMethods()方法一个用途是Server注册时存储所有的method的ServcieSescriptor。invoke()方法用于执行指定实例对象的method。
# 4 Server 模块
> 本项目最核心两个模块之一，主要作用是定义了处理client请求的方法。
>> 4.1 RpcServer类实现如下：
```
@Slf4j
public class RpcServer {
    private RPCServerConfig config;
    private TransportServer net;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceManager serviceManager;
    private ServiceInvoker serviceInvoker;

    public RpcServer() {

    }

    public RpcServer(RPCServerConfig config) {
        this.config = config;

        //net
        this.net = ReflectionUtils.newInstance(config.getTransportClass());
        this.net.init(config.getPort(),this.handler);

        //codec
        this.encoder = ReflectionUtils.newInstance(config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(config.getDecoderClass());

        //service
        this.serviceManager = new ServiceManager();
        this.serviceInvoker = new ServiceInvoker();
    }


    public <T> void register(Class<T> interfaceClasss,T bean){
        serviceManager.register(interfaceClasss,bean);
    }

    public void start(){
        this.net.start();
    }

    public void stop(){
        this.net.stop();
    }

    private RequestHandler handler = new RequestHandler() {
        @Override
        public void onRequest(InputStream recive, OutputStream toResp) {
            Response resp = new Response();
            try {
                byte[] inBytes = new byte[recive.available()];

                IOUtils.readFully(recive,inBytes,0,recive.available());
               // byte[] inBytes = IOUtils.readFully(recive,recive.available());
                Request request = decoder.decode(inBytes,Request.class);//这里是从client传过来的

                log.info("get request: {}",request);

                ServiceInstance sis = serviceManager.lookup(request );//request封装了ServiceDescriptor描述字符串
                Object ret = serviceInvoker.invoke(sis,request);//执行返回结果

                resp.setData(ret);

            } catch (Exception e) {
                log.warn(e.getMessage(),e);
                resp.setCode(1);
                resp.setMessage("RpcServer got error"+e.getClass().getName()+" : "+e.getMessage());

            }finally {
                try {
                    byte[] outBytes = encoder.encode(resp);
                    toResp.write(outBytes);
                    log.info("response client");
                } catch (IOException e) {
                    log.warn(e.getMessage(),e);
                }
            }
        }
    };
}
```
>> 该方法初时较复杂，理清各个类之后将比较明了。上述RpcServerConfig主要用于常量配置的定义，Encoder与Decoder分别为编码器与解码器不做过多解释。  
>> ServiceManager类的实现如下:
```
@Slf4j
public class ServiceManager {
    private Map<ServiceDescriptor,ServiceInstance> services;

    public ServiceManager(){
        this.services = new ConcurrentHashMap<>();
    }
    public <T> void register(Class<T> interfaceClass, T bean){
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        for (Method method :methods){
            ServiceInstance sis = new ServiceInstance(bean,method);
            ServiceDescriptor sdp = ServiceDescriptor.from(interfaceClass,method);

            services.put(sdp,sis);
            log.info("register service {} {}",sdp.getClazz(),sdp.getMethod());
        }
    }

    public ServiceInstance lookup(Request request){
        ServiceDescriptor sdp = request.getServiceDescriptor();
        return services.get(sdp);
    }
}
```
>> register()方法主要用于注册该class的所有共有方法，并且获取之前讲述的ServiceDescriptor实例与ServiceInstance作为键值对的形式存储。**（需要注意的是这里的registetr方法的参数bean正是需要执行的实例对象）**其ServiceInstance类的定义如下：
```
@Data
@AllArgsConstructor
public class ServiceInstance {
    private Object target;
    private Method method;
}
```
>> 其内部主要定义了连个变量，一个是需要执行某个method的目标对象，另一个是需要执行的method。**（到这里应该认识了ServiceManager的真正作用，存储method的描述与实例的对应关系，方便通过client的传参进行get）**  
>> 回到上述初始类RpcServer，最需要注意的是RequestHandler的实现。其onRequest()方法通过Servlet的inputStream与OutputStream参数获取来自Client的数据，并且通过获取到的Request实例参数从ServiceManager中get实例对象与method。因为Request对象中包含有Client获取到的实际参数，因此将上述参数一起传递到ServiceInvoker对象进行执行。该类实现如下：  
```
public class ServiceInvoker {
    public Object invoke(ServiceInstance service, Request request){
        return ReflectionUtils.invoke(service.getTarget(),service.getMethod(),request.getParameters());
    }
}
```
>> 上述代码最终只是调用common模块的反射工具封装执行。  
## 5 Client 模块
> 该模块主要功能有连个一个时动态代理获取实参，一个是请求Server进行过程调用。
>> 其RpcClient类实现如下：
```
public class RpcClient {
    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RpcClient(RpcClientConfig config) {
        this.config = config;
        this.encoder = ReflectionUtils.newInstance(this.config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(this.config.getDecoderClass());
        this.selector = ReflectionUtils.newInstance(this.config.getSelectorClass());

        this.selector.init(this.config.getServers(),
                this.config.getConnectCount(),
                this.config.getTransportClass());

    }

    public RpcClient() {
        this(new RpcClientConfig());
    }

    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{clazz},
                new RemoteInvoker(clazz,encoder,decoder,selector)
        );
    }
}
```
>> 该类需要注意两点，之一是TransportSelector对象，其实现如下：
```
@Slf4j
public class RandomTransportSelector implements TransportSelector {

    /**
     * 已经连接好的client
     */
    private List<TransportClient> clients;
    public RandomTransportSelector() {
        clients = new ArrayList<>();
    }

    @Override
    public synchronized void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz) {
        count = Math.max(count,1);

        for (Peer peer : peers){
            for (int i =0;i<count;i++){
                TransportClient client = ReflectionUtils.newInstance(clazz);
                client.connect(peer);
                clients.add(client);
            }
            log.info("connect server: {} ",peer);
        }
    }

    @Override
    public synchronized TransportClient select() {
        int i = new Random().nextInt(clients.size());
        return clients.remove(i);
    }

    @Override
    public synchronized void release(TransportClient client) {
        clients.add(client);
    }

    @Override
    public synchronized void close() {
        for (TransportClient client :clients){
            client.close();
        }
        clients.clear();
    }
}
```
>> 该类主要是用于处理Client对Server的连接问题，相当于连接池，由有需求时随机返回连接。
>> 回到上述RpcClient类的getProxy()方法为动态代理，不为此介绍的重点，但是需要重点关注RemoteInvoker类，实现如下：
```
Slf4j
public class RemoteInvoker implements InvocationHandler{

    private Class clazz;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RemoteInvoker(Class clazz , Encoder encoder, Decoder decoder, TransportSelector selector) {
        this.clazz = clazz;
        this.decoder = decoder;
        this.encoder = encoder;
        this.selector = selector;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Request request = new Request();
        request.setServiceDescriptor(ServiceDescriptor.from(clazz,method));
        request.setParameters(args);

        Response response = invokeRemote(request);
        if(response==null || response.getCode()!=0){
            throw new IllegalStateException("fail to invoke remote: "+response);
        }
        return response.getData();
    }
    private Response invokeRemote(Request request){
        TransportClient client = null;
        Response response = null;
        try{
            client = selector.select();

            byte[] outBytes = encoder.encode(request);
            InputStream revice = client.write(new ByteArrayInputStream(outBytes));

            byte[] inBytes = new byte[revice.available()];
            IOUtils.readFully(revice,inBytes,0,revice.available());

           // byte[] inBytes = IOUtils.readFully(revice , revice.available());

             response = decoder.decode(inBytes,Response.class);

        }catch (IOException e) {
            log.warn(e.getMessage(),e);
            response = new Response();
            response.setCode(1);
            response.setMessage("RpcClient got error:"+e.getClass()+" : "+e.getMessage());
        }finally {
            if(client!=null){
                selector.release(client);
            }
        }
        return response;
    }
}

```
>> 上述代码需要关注invoke()方法中对代理方法的参数进行存储封装到Request对象并且最终序列化传递到Server。至此本项目个关键模块实现与执行流程介绍完毕。
  
# 下一步 
> * 线程池
> * 注册中心
> * 数据安全传输
> * 流行框架集成
