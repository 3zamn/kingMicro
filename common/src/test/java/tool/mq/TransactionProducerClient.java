package tool.mq;

/*public class TransactionProducerClient {
	 private final static Logger log = ClientLogger.getLog(); // 您需要设置自己的日志，便于排查问题
	 public static void main(String[] args) throws InterruptedException {
	     final BusinessService businessService = new BusinessService(); // 本地业务 Service
	     Properties properties = new Properties();
	     // 您在控制台创建的 Producer ID 注意：事务消息的 Producer ID 不能与其他类型消息的 Producer ID 共用
	     properties.put(PropertyKeyConst.ProducerId, "");
	     // 阿里云身份验证，在阿里云服务器管理控制台创建
	     properties.put(PropertyKeyConst.AccessKey, "");
	     // 阿里云身份验证，在阿里云服务器管理控制台创建
	     properties.put(PropertyKeyConst.SecretKey, "");
	     // 设置 TCP 接入域名（此处以公共云生产环境为例）
	     properties.put(PropertyKeyConst.ONSAddr,
	       "http://onsaddr-internal.aliyun.com:8080/rocketmq/nsaddr4client-internal");
	     TransactionProducer producer = ONSFactory.createTransactionProducer(properties,
	             new LocalTransactionCheckerImpl());
	     producer.start();
	     Message msg = new Message("Topic", "TagA", "Hello MQ transaction===".getBytes());
	     try {
	             SendResult sendResult = producer.send(msg, new LocalTransactionExecuter() {
	                 @Override
	                 public TransactionStatus execute(Message msg, Object arg) {
	                     // 消息 ID（有可能消息体一样，但消息 ID 不一样，当前消息 ID 在控制台无法查询）
	                     String msgId = msg.getMsgID();
	                     // 消息体内容进行 crc32，也可以使用其它的如 MD5
	                     long crc32Id = HashUtil.crc32Code(msg.getBody());
	                     // 消息 ID 和 crc32id 主要是用来防止消息重复
	                     // 如果业务本身是幂等的，可以忽略，否则需要利用 msgId 或 crc32Id 来做幂等
	                     // 如果要求消息绝对不重复，推荐做法是对消息体 body 使用 crc32或 md5来防止重复消息
	                     Object businessServiceArgs = new Object();
	                     TransactionStatus transactionStatus = TransactionStatus.Unknow;
	                     try {
	                         boolean isCommit =
	                             businessService.execbusinessService(businessServiceArgs);
	                         if (isCommit) {
	                             // 本地事务成功则提交消息
	                             transactionStatus = TransactionStatus.CommitTransaction;
	                         } else {
	                             // 本地事务失败则回滚消息
	                             transactionStatus = TransactionStatus.RollbackTransaction;
	                         }
	                     } catch (Exception e) {
	                         log.error("Message Id:{}", msgId, e);
	                     }
	                     System.out.println(msg.getMsgID());
	                     log.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
	                     return transactionStatus;
	                 }
	             }, null);
	         }
	         catch (Exception e) {
	             // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
	             System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
	             e.printStackTrace();
	         }
	     // demo example 防止进程退出(实际使用不需要这样)
	     TimeUnit.MILLISECONDS.sleep(Integer.MAX_VALUE);
	 }
	}
*/