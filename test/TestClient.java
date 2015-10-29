package cooxm.devicecontrol.util;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Sep 6, 2015 3:30:06 PM 
 */


 
import org.apache.cassandra.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
 
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

public class TestClient {
 
    public static void main(String[] args)
            throws TException, InvalidRequestException, UnavailableException, UnsupportedEncodingException, NotFoundException, TimedOutException {
        TTransport tr = new TFramedTransport(new TSocket("120.24.81.226", 9042));
        TProtocol proto = new TBinaryProtocol(tr);
        Cassandra.Client client = new Cassandra.Client(proto);
        tr.open();
 
        String key_user_id = "1234";     //row id
 
        client.set_keyspace("NOAH");     //指定keyspace
        ColumnParent parent = new ColumnParent("Users");
        ColumnPath path = new ColumnPath("Users");
 
        // 读取单列内容
        path.setColumn(toByteBuffer("name"));
        ColumnOrSuperColumn col = (client.get(toByteBuffer(key_user_id), path, ConsistencyLevel.ONE));
        System.out.println(toString(col.column.name) + " -> " + toString(col.column.value));
 
        // 读取全部列内容
        SlicePredicate predicate = new SlicePredicate();
        SliceRange sliceRange = new SliceRange(toByteBuffer(""), toByteBuffer(""), false, 10);
        predicate.setSlice_range(sliceRange);
 
        List<ColumnOrSuperColumn> results = client.get_slice(toByteBuffer(key_user_id), parent, predicate, ConsistencyLevel.ONE);
        for (ColumnOrSuperColumn result : results) {
            Column column = result.column;
            System.out.println(toString(column.name) + " -> " + toString(column.value));
        }
 
        // 向数据库中插入数据
        long currentTimeMillis = System.currentTimeMillis();
        long nanoTime = System.nanoTime();
        long currentTimeMicros = currentTimeMillis * 1000 + nanoTime / 1000 - (nanoTime > 1000000 ? (nanoTime / 1000000) * 1000 : 0);
 
    //注意取microseconds，而不是milliseconds，因为其它客户机都是microseconds。
    //如果使用System.currentTimeMillis()导致你的时间永远小于其它客户机的时间，使其操作会被忽略。
        System.out.println(currentTimeMicros);
 
        String new_key_user_id = "123456";     //row id
 
        Column nameColumn = new Column(toByteBuffer("name"));
        nameColumn.setValue(toByteBuffer("Chris Goffinet"));
        nameColumn.setTimestamp(currentTimeMicros);
        client.insert(toByteBuffer(new_key_user_id), parent, nameColumn, ConsistencyLevel.ONE);
 
        Column pwdColumn = new Column(toByteBuffer("password"));
        pwdColumn.setValue(toByteBuffer("12345678"));
        pwdColumn.setTimestamp(currentTimeMicros);
        client.insert(toByteBuffer(new_key_user_id), parent, pwdColumn, ConsistencyLevel.ONE);
 
        tr.flush();
        tr.close();
    }
 
    public static ByteBuffer toByteBuffer(String value)
            throws UnsupportedEncodingException {
        return ByteBuffer.wrap(value.getBytes("UTF-8"));
    }
 
    public static String toString(ByteBuffer buffer)
            throws UnsupportedEncodingException {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, "UTF-8");
    }
}
