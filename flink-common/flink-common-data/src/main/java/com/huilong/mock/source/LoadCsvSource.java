package com.huilong.mock.source;

import com.huilong.mock.dto.StockTransaction;
import com.huilong.mock.dto.UserBehavior;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 加载  csv 数据
 *
 * @author daocr
 * @date 2020/11/25
 */
public class LoadCsvSource {

    /**
     * @param env
     * @param path UserBehavior.csv 的本地文件路径
     * @return
     * @throws URISyntaxException
     */
    public static <T extends Object> DataStream<T> load(StreamExecutionEnvironment env, String path, String[] fieldOrder, T pojoTypeClass, Boolean skipFirstLineAsHeader) throws URISyntaxException {

        Path filePath = Path.fromLocalFile(new File(path));
        // 抽取 UserBehavior 的 TypeInformation，是一个 PojoTypeInfo
//        PojoTypeInfo<UserBehavior> pojoType = (PojoTypeInfo<UserBehavior>) TypeExtractor.createTypeInfo(UserBehavior.class);
        PojoTypeInfo<T> pojoType = (PojoTypeInfo<T>) TypeExtractor.createTypeInfo(pojoTypeClass.getClass());
        // 由于 Java 反射抽取出的字段顺序是不确定的，需要显式指定下文件中字段的顺序
//        String[] fieldOrder = new String[]{"userId", "itemId", "categoryId", "behavior", "timestamp"};
        // 创建 PojoCsvInputFormat
        PojoCsvInputFormat<T> csvInput = new PojoCsvInputFormat<T>(filePath, pojoType, fieldOrder);
        csvInput.setSkipFirstLineAsHeader(skipFirstLineAsHeader);
        DataStream<T> dataSource = env.createInput(csvInput, pojoType);

        return dataSource;

    }

    public static <T> DataStream<StockTransaction> loadStockTransaction(StreamExecutionEnvironment env, String path) throws URISyntaxException {

        String[] fieldOrder = new String[]{"transactionTime", "transactionPrice", "priceChange", "transactionCut", "transactionAmount", "transactionType"};
        DataStream<StockTransaction> load = load(env, path, fieldOrder, new StockTransaction(), Boolean.TRUE);

        return load;
    }

}
