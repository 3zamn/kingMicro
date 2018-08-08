package com.king.common.utils.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
 
/**
 * @author King chen
 * @emai 396885563@qq.com
 * @data2018年4月20日
 */
public class HttpUtils {
     
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
     
    private HttpRequestBase request; //请求对象
    private EntityBuilder builder; //Post, put请求的参数
    private URIBuilder uriBuilder; //get, delete请求的参数
    private LayeredConnectionSocketFactory socketFactory; //连接工厂
    private HttpClientBuilder clientBuilder; //构建httpclient
    private CloseableHttpClient httpClient; //
    private CookieStore cookieStore; //cookie存储器
    private Builder config; //请求的相关配置
    private boolean isHttps; //是否是https请求
    private int type; //请求类型1-post, 2-get, 3-put, 4-delete
 
     
     
    private HttpUtils (HttpRequestBase request) {
        this.request = request;
         
        this.clientBuilder = HttpClientBuilder.create();
        this.isHttps = request.getURI().getScheme().equalsIgnoreCase("https");
        this.config = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY);
        this.cookieStore = new BasicCookieStore();
         
        if (request instanceof HttpPost) {
            this.type = 1;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());
             
        } else if(request instanceof HttpGet) {
            this.type = 2;
            this.uriBuilder = new URIBuilder();
             
        } else if(request instanceof HttpPut) {
            this.type = 3;
            this.builder = EntityBuilder.create().setParameters(new ArrayList<NameValuePair>());
         
        } else if(request instanceof HttpDelete) {
            this.type = 4;
            this.uriBuilder = new URIBuilder();
        }
    }
     
    private HttpUtils(HttpRequestBase request, HttpUtils clientUtils) {
        this(request);
        this.httpClient = clientUtils.httpClient;
        this.config = clientUtils.config;
        this.setHeaders(clientUtils.getAllHeaders());
        this.SetCookieStore(clientUtils.cookieStore);
        this.setContentType(clientUtils.builder.getContentType());
    }
     
    private static HttpUtils create(HttpRequestBase request) {
        return new HttpUtils(request);
    }
     
    private static HttpUtils create(HttpRequestBase request, HttpUtils clientUtils) {
        return new HttpUtils(request, clientUtils);
    }
 
    /**
     * 创建post请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils post(String url) {
        return create(new HttpPost(url));
    }
     
    /**
     * 创建get请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils get(String url) {
        return create(new HttpGet(url));
    }
     
    /**
     * 创建put请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils put(String url) {
        return create(new HttpPut(url));
    }
     
    /**
     * 创建delete请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils delete(String url) {
        return create(new HttpDelete(url));
    }
     
    /**
     * 创建post请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils post(URI uri) {
        return create(new HttpPost(uri));
    }
     
    /**
     * 创建get请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils get(URI uri) {
        return create(new HttpGet(uri));
    }
     
    /**
     * 创建put请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils put(URI uri) {
        return create(new HttpPut(uri));
    }
     
    /**
     * 创建delete请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils delete(URI uri) {
        return create(new HttpDelete(uri));
    }
     
    /**
     * 创建post请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils post(String url, HttpUtils clientUtils) {
        return create(new HttpPost(url), clientUtils);
    }
     
    /**
     * 创建get请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils get(String url, HttpUtils clientUtils) {
        return create(new HttpGet(url), clientUtils);
    }
     
    /**
     * 创建put请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils put(String url, HttpUtils clientUtils) {
        return create(new HttpPut(url), clientUtils);
    }
     
    /**
     * 创建delete请求
     * 
     * @param url 请求地址
     * @return
     */
    public static HttpUtils delete(String url, HttpUtils clientUtils) {
        return create(new HttpDelete(url), clientUtils);
    }
     
    /**
     * 创建post请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils post(URI uri, HttpUtils clientUtils) {
        return create(new HttpPost(uri), clientUtils);
    }
     
    /**
     * 创建get请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils get(URI uri, HttpUtils clientUtils) {
        return create(new HttpGet(uri), clientUtils);
    }
     
    /**
     * 创建put请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils put(URI uri, HttpUtils clientUtils) {
        return create(new HttpPut(uri), clientUtils);
    }
     
    /**
     * 创建delete请求
     * 
     * @param uri 请求地址
     * @return
     */
    public static HttpUtils delete(URI uri, HttpUtils clientUtils) {
        return create(new HttpDelete(uri), clientUtils);
    }
     
    /**
     * 添加参数
     * 
     * @param parameters
     * @return
     */
    public HttpUtils setParameters(final NameValuePair ...parameters) {
        if (builder != null) {
            builder.setParameters(parameters);
        } else {
            uriBuilder.setParameters(Arrays.asList(parameters));
        }
        return this;
    }
     
    /**
     * 添加参数
     * 
     * @param name
     * @param value
     * @return
     */
    public HttpUtils addParameter(final String name, final String value) {
        if (builder != null) {
            builder.getParameters().add(new BasicNameValuePair(name, value));
        } else {
            uriBuilder.addParameter(name, value);
        }
        return this;
    }
     
    /**
     * 添加参数
     * 
     * @param parameters
     * @return
     */
    public HttpUtils addParameters(final NameValuePair ...parameters) {
        if (builder != null) {
            builder.getParameters().addAll(Arrays.asList(parameters));
        } else {
            uriBuilder.addParameters(Arrays.asList(parameters));
        }
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param parameters
     * @return
     */
    public HttpUtils setParameters(final Map<String, String> parameters) {
        NameValuePair [] values = new NameValuePair[parameters.size()];
        int i = 0;
         
        for (Entry<String, String> parameter : parameters.entrySet()) {
            values[i++] = new BasicNameValuePair(parameter.getKey(), parameter.getValue());
        }
         
        setParameters(values);
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param file
     * @return
     */
    public HttpUtils setParameter(final File file) {
        if(builder != null) {
            builder.setFile(file);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param binary
     * @return
     */
    public HttpUtils setParameter(final byte[] binary) {
        if(builder != null) {
            builder.setBinary(binary);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param serializable
     * @return
     */
    public HttpUtils setParameter(final Serializable serializable) {
        if(builder != null) {
            builder.setSerializable(serializable);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
     
    /**
     * 设置参数为Json对象
     * 
     * @param parameter 参数对象
     * @return
     */
    public HttpUtils setParameterJson(final Object parameter) {
        if(builder != null) {
            try {
                builder.setBinary(mapper.writeValueAsBytes(parameter));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param stream
     * @return
     */
    public HttpUtils setParameter(final InputStream stream) {
        if(builder != null) {
            builder.setStream(stream);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }
     
    /**
     * 设置请求参数,会覆盖之前的参数
     * 
     * @param text
     * @return
     */
    public HttpUtils setParameter(final String text) {
        if(builder != null) {
            builder.setText(text);
        } else {
            uriBuilder.setParameters(URLEncodedUtils.parse(text, Consts.UTF_8));
        }
        return this;
    }
     
    /**
     * 设置内容编码
     * 
     * @param encoding
     * @return
     */
    public HttpUtils setContentEncoding(final String encoding) {
        if(builder != null) builder.setContentEncoding(encoding);
        return this;
    }
     
    /**
     * 设置ContentType
     * 
     * @param contentType
     * @return
     */
    public HttpUtils setContentType(ContentType contentType) {
        if(builder != null) builder.setContentType(contentType);
        return this;
    }
     
    /**
     * 设置ContentType
     * 
     * @param mimeType
     * @param charset 内容编码
     * @return
     */
    public HttpUtils setContentType(final String mimeType, final Charset charset) {
        if(builder != null) builder.setContentType(ContentType.create(mimeType, charset));
        return this;
    }
     
    /**
     * 添加参数
     * 
     * @param parameters
     * @return
     */
    public HttpUtils addParameters(Map<String, String> parameters) {
        List<NameValuePair> values = new ArrayList<NameValuePair>(parameters.size());
         
        for (Entry<String, String> parameter : parameters.entrySet()) {
            values.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
        }
         
        if(builder != null) {
            builder.getParameters().addAll(values);
        } else {
            uriBuilder.addParameters(values);
        }
        return this;
    }
     
    /**
     * 添加Header
     * 
     * @param name
     * @param value
     * @return
     */
    public HttpUtils addHeader(String name, String value) {
        request.addHeader(name, value);
        return this;
    }
     
    /**
     * 添加Header
     * 
     * @param headers
     * @return
     */
    public HttpUtils addHeaders(Map<String, String> headers) {
        for (Entry<String, String> header : headers.entrySet()) {
            request.addHeader(header.getKey(), header.getValue());
        }
         
        return this;
    }
     
    /**
     * 设置Header,会覆盖所有之前的Header
     * 
     * @param headers
     * @return
     */
    public HttpUtils setHeaders(Map<String, String> headers) {
        Header [] headerArray = new Header[headers.size()];
        int i = 0;
         
        for (Entry<String, String> header : headers.entrySet()) {
            headerArray[i++] = new BasicHeader(header.getKey(), header.getValue());
        }
         
        request.setHeaders(headerArray);
        return this;
    }
     
    public HttpUtils setHeaders(Header [] headers) {
        request.setHeaders(headers);
        return this;
    }
     
    /**
     * 获取所有Header
     * 
     * @return
     */
    public Header[] getAllHeaders() {
        return request.getAllHeaders();
    }
     
    /**
     * 移除指定name的Header列表
     * 
     * @param name
     */
    public HttpUtils removeHeaders(String name){
        request.removeHeaders(name);
        return this;
    }
     
    /**
     * 移除指定的Header
     * 
     * @param header
     */
    public HttpUtils removeHeader(Header header){
        request.removeHeader(header);
        return this;
    }
     
    /**
     * 移除指定的Header
     * 
     * @param name
     * @param value
     */
    public HttpUtils removeHeader(String name, String value){
        request.removeHeader(new BasicHeader(name, value));
        return this;
    }
     
    /**
     * 是否存在指定name的Header
     * 
     * @param name
     * @return
     */
    public boolean containsHeader(String name){
        return request.containsHeader(name);
    }
     
    /**
     * 获取Header的迭代器
     * 
     * @return
     */
    public HeaderIterator headerIterator(){
        return request.headerIterator();
    }
     
    /**
     * 获取协议版本信息
     * 
     * @return
     */
    public ProtocolVersion getProtocolVersion(){
        return request.getProtocolVersion();
    }
     
    /**
     * 获取请求Url
     * 
     * @return
     */
    public URI getURI(){
        return request.getURI();
    }
     
    /**
     * 设置请求Url
     * 
     * @return
     */
    public HttpUtils setURI(URI uri){
        request.setURI(uri);
        return this;
    }
     
    /**
     * 设置请求Url
     * 
     * @return
     */
    public HttpUtils setURI(String uri){
        return setURI(URI.create(uri));
    }
     
    /**
     * 设置一个CookieStore
     * 
     * @param cookieStore
     * @return
     */
    public HttpUtils SetCookieStore(CookieStore cookieStore){
        if(cookieStore == null) return this;
        this.cookieStore = cookieStore;
        return this;
    }
     
    /**
     * 添加Cookie
     * 
     * @param cookie
     * @return
     */
    public HttpUtils addCookie(Cookie ...cookies){
        if(cookies == null) return this;
         
        for (int i = 0; i < cookies.length; i++) {
            cookieStore.addCookie(cookies[i]);
        }
        return this;
    }
     
    /**
     * 设置网络代理
     * 
     * @param hostname
     * @param port
     * @return
     */
    public HttpUtils setProxy(String hostname, int port) {
        HttpHost proxy = new HttpHost(hostname, port);
        return setProxy(proxy);
    }
     
    /**
     * 设置网络代理
     * 
     * @param hostname
     * @param port
     * @param schema
     * @return
     */
    public HttpUtils setProxy(String hostname, int port, String schema) {
        HttpHost proxy = new HttpHost(hostname, port, schema);
        return setProxy(proxy);
    }
     
    /**
     * 设置网络代理
     * 
     * @param address
     * @return
     */
    public HttpUtils setProxy(InetAddress address) {
        HttpHost proxy = new HttpHost(address);
        return setProxy(proxy);
    }
     
    /**
     * 设置网络代理
     * 
     * @param host
     * @return
     */
    public HttpUtils setProxy(HttpHost host) {
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(host);
        clientBuilder.setRoutePlanner(routePlanner);
        return this;
    }
     
    /**
     * 设置双向认证的JKS
     * 
     * @param jksFilePath jks文件路径
     * @param password 密码
     * @return
     */
    public HttpUtils setJKS(String jksFilePath, String password) {
        return setJKS(new File(jksFilePath), password);
    }
     
    /**
     * 设置双向认证的JKS
     * 
     * @param jksFile jks文件
     * @param password 密码
     * @return
     */
    public HttpUtils setJKS(File jksFile, String password) {
        try (InputStream instream = new FileInputStream(jksFile)) {
            return setJKS(instream, password);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
    /**
     * 设置双向认证的JKS, 不会关闭InputStream
     * 
     * @param instream jks流
     * @param password 密码
     * @return
     */
    public HttpUtils setJKS(InputStream instream, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
            keyStore.load(instream, password.toCharArray());
            return setJKS(keyStore);
             
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
    /**
     * 设置双向认证的JKS
     * 
     * @param keyStore jks
     * @return
     */
    public HttpUtils setJKS(KeyStore keyStore) {
        try {
            SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(keyStore).build();
            socketFactory = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
         
        return this;
    }
     
    /**
     * 设置Socket超时时间,单位:ms
     * 
     * @param socketTimeout
     * @return
     */
    public HttpUtils setSocketTimeout(int socketTimeout){
        config.setSocketTimeout(socketTimeout);
        return this;
    }
     
    /**
     * 设置连接超时时间,单位:ms
     * 
     * @param connectTimeout
     * @return
     */
    public HttpUtils setConnectTimeout(int connectTimeout) {
        config.setConnectTimeout(connectTimeout);
        return this;
    }
     
    /**
     * 设置请求超时时间,单位:ms
     * 
     * @param connectionRequestTimeout
     * @return
     */
    public HttpUtils setConnectionRequestTimeout(int connectionRequestTimeout) {
        config.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }
     
    /**
     * 设置是否允许服务端循环重定向
     * 
     * @param circularRedirectsAllowed
     * @return
     */
    public HttpUtils setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
        config.setCircularRedirectsAllowed(circularRedirectsAllowed);
        return this;
    }
     
    /**
     * 设置是否启用调转
     * 
     * @param redirectsEnabled
     * @return
     */
    public HttpUtils setRedirectsEnabled(boolean redirectsEnabled) {
        config.setRedirectsEnabled(redirectsEnabled);
        return this;
    }
     
    /**
     * 设置重定向的次数
     * 
     * @param maxRedirects
     * @return
     */
    public HttpUtils maxRedirects(int maxRedirects){
        config.setMaxRedirects(maxRedirects);
        return this;
    }
     
    /**
     * 执行请求
     * 
     * @return
     */
    public ResponseWrap execute() {
        settingRequest();
        if(httpClient == null) {
            httpClient = clientBuilder.build();
        }
         
        try {
            HttpClientContext context = HttpClientContext.create();
            CloseableHttpResponse response = httpClient.execute(request, context);
            return new ResponseWrap(httpClient, request, response, context, mapper);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
 
    /**
     * 执行请求
     * 
     * @param responseHandler
     * @return
     */
    public <T> T execute(final ResponseHandler<? extends T> responseHandler) {
        settingRequest();
        if(httpClient == null) httpClient = clientBuilder.build();
         
        try {
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
 
    /**
     * 关闭连接
     * 
     */
    @SuppressWarnings("deprecation")
    public void shutdown(){
        httpClient.getConnectionManager().shutdown();
    }
     
    /**
     * 获取LayeredConnectionSocketFactory 使用ssl单向认证
     * 
     * @return
     */
    private LayeredConnectionSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
 
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return sslsf;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
     
    private void settingRequest() {
        URI uri = null;
        if(uriBuilder != null && uriBuilder.getQueryParams().size() != 0) {
            try {
                uri = uriBuilder.setPath(request.getURI().toString()).build();
            } catch (URISyntaxException e) {
                logger.warn(e.getMessage(), e);
            }
        }
         
        HttpEntity httpEntity = null;
         
        switch (type) {
        case 1:
            httpEntity = builder.build();
            if(httpEntity.getContentLength() > 0) ((HttpPost)request).setEntity(builder.build());
            break;
             
        case 2:
            HttpGet get = ((HttpGet)request);
            if (uri != null)  get.setURI(uri);
            break;
         
        case 3:
            httpEntity = builder.build();
            if(httpEntity.getContentLength() > 0) ((HttpPut)request).setEntity(httpEntity);
            break;
             
        case 4:
            HttpDelete delete = ((HttpDelete)request);
            if (uri != null) delete.setURI(uri);
             
            break;
        }
         
        if (isHttps && socketFactory != null ) {
            clientBuilder.setSSLSocketFactory(socketFactory);
         
        } else if(isHttps) {
            clientBuilder.setSSLSocketFactory(getSSLSocketFactory());
        }
         
        clientBuilder.setDefaultCookieStore(cookieStore);
        request.setConfig(config.build());
    }
     
     
 
    //json转换器
    public static ObjectMapper mapper = new ObjectMapper();
    static{
        mapper.setSerializationInclusion(Include.NON_DEFAULT);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.getFactory().enable(Feature.ALLOW_COMMENTS);
        mapper.getFactory().enable(Feature.ALLOW_SINGLE_QUOTES);
    }
    
    public static void main(String[] args) throws Exception{
        String url = "http://localhost:8888/portal-web/test.do?TextUserName=admin&TextPassword=111111";
        HttpUtils http = HttpUtils.post(url);
        http.setContentType("application/x-www-form-urlencoded", Consts.UTF_8);
//      http.addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");//这样设置无效，必须使用http.setContentype
//      http.addParameter("TextUserName", "admin"); //用户名
//      http.addParameter("TextPassword", "111111"); //密码
        ResponseWrap response = http.execute(); //执行请求
        System.out.println(response.getString()); //输出内容
        
     
        url = "http://localhost:8888/portal-web/test.do?op=add";
        http = HttpUtils.post(url,http);
        http.addParameter("txtSignerName", "1"); //
      
        response = http.execute(); //执行请求
        System.out.println(response.getString()); //输出内容
        response.transferTo("d:/baidu-search-java.html"); //输出到文件
        
        http.shutdown();
    }
    
    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param)
    {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try
        {
            String urlNameString = url + "?" + param;
            logger.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            logger.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            logger.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (SocketTimeoutException e)
        {
            logger.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (IOException e)
        {
            logger.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e.getMessage());
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception ex)
            {
                logger.error("调用in.close Exception, url=" + url + ",param=" + param, ex.getMessage());
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            String urlNameString = url + "?" + param;
            logger.info("sendPost - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            logger.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            logger.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (SocketTimeoutException e)
        {
            logger.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (IOException e)
        {
            logger.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, e.getMessage());
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                logger.error("调用in.close Exception, url=" + url + ",param=" + param, ex.getMessage());
            }
        }
        return result.toString();
    }

    @SuppressWarnings("deprecation")
    public static String sendSSLPost(String url, String param)
    {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;
        try
        {
            logger.info("sendSSLPost - {}", urlNameString);
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URL console = new URL(urlNameString);
            HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            DataInputStream indata = new DataInputStream(is);
            String ret = "";
            while (ret != null)
            {
                ret = indata.readLine();
                if (ret != null && !ret.trim().equals(""))
                {
                    result.append(new String(ret.getBytes("ISO-8859-1"), "utf-8"));
                }
            }
            logger.info("recv - {}", result);
            conn.disconnect();
            indata.close();
        }
        catch (ConnectException e)
        {
            logger.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (SocketTimeoutException e)
        {
            logger.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (IOException e)
        {
            logger.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e.getMessage());
        }
        return result.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new X509Certificate[] {};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }
    }
}