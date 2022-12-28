package org.example.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * mybatis plus generator test
 */
public class MyBatisPlusGenerator {

    public static void main(String[] args) {
        //1. global config
        GlobalConfig config = new GlobalConfig();
                // author
        config.setAuthor("Bob")
                // get path, better use absolute path, and move to project
                //TODO  TODO  TODO  TODO
                .setOutputDir("/Users/linbob/Desktop/temp/demo/src/main/java")
                // override
                .setFileOverride(true)
                // primary id
                .setIdType(IdType.AUTO)
                .setDateType(DateType.ONLY_DATE)
                // set service name
                .setServiceName("%sService")
                // set entity name
                .setEntityName("%sDO")
                // generate basic resultMap
                .setBaseResultMap(true)
                // do not use ar
                .setActiveRecord(false)
                // generate basic sql
                .setBaseColumnList(true);

        //2. data source config
        DataSourceConfig dsConfig = new DataSourceConfig();
        // config database type
        dsConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                //TODO  TODO  TODO  TODO
                .setUrl("jdbc:mysql://120.24.94.91:3306/xd_product?useSSL=false")
                .setUsername("root")
                .setPassword("xdclass.net168");

        //3. strategy config globalConfiguration
        StrategyConfig stConfig = new StrategyConfig();

        // global uppercase
        stConfig.setCapitalMode(true)
                // name strategy
                .setNaming(NamingStrategy.underline_to_camel)
                // use lombok
                .setEntityLombokModel(true)
                // use restcontroller annotation
                .setRestControllerStyle(true)

                // generate table, suport multiple tables in array
                //TODO  TODO  TODO  TODO
//                .setInclude("banner","product");
                .setInclude("product_task");

        //4. package naming strategy
        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("org.example")
                .setMapper("mapper")
                .setService("service")
                .setController("controller")
                .setEntity("model")
                .setXml("mapper");

        //5. integration config
        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config)
                .setDataSource(dsConfig)
                .setStrategy(stConfig)
                .setPackageInfo(pkConfig);

        //6. execute operation
        ag.execute();
        System.out.println("======= code generate successfully  ========");
    }
}