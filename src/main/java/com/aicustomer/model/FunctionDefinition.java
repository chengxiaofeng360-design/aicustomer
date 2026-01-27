package com.aicustomer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Function Calling 工具定义
 * 
 * 定义AI可以调用的工具函数schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDefinition {

        /**
         * 工具类型，固定为"function"
         */
        private String type = "function";

        /**
         * 函数详细信息
         */
        private Function function;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Function {
                /**
                 * 函数名称（唯一标识）
                 */
                private String name;

                /**
                 * 函数描述（帮助AI理解何时调用）
                 */
                private String description;

                /**
                 * 参数schema（JSON Schema格式）
                 */
                private Parameters parameters;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Parameters {
                /**
                 * 类型，通常是"object"
                 */
                private String type = "object";

                /**
                 * 属性列表
                 */
                private Map<String, Property> properties;

                /**
                 * 必需的参数列表
                 */
                private List<String> required;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Property {
                /**
                 * 参数类型（string, number, boolean, array, object）
                 */
                private String type;

                /**
                 * 参数描述
                 */
                private String description;

                /**
                 * 枚举值（可选）
                 */
                private List<String> enumValues;
        }

        /**
         * 创建获取客户总数的函数定义
         */
        public static FunctionDefinition getCustomerCount() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("get_customer_count")
                                                .description("获取系统中客户的总数量。当用户询问'有多少客户'、'客户数量'、'总共多少客户'等问题时调用。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of())
                                                                .required(List.of())
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 创建获取客户列表的函数定义
         */
        public static FunctionDefinition getCustomerList() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("get_customer_list")
                                                .description("获取客户详细列表。当用户想要查看具体的客户信息、客户名单、客户是谁等时调用。可以按地区、等级筛选。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of(
                                                                                "region", Property.builder()
                                                                                                .type("string")
                                                                                                .description("地区筛选，如'北京'、'上海'、'广东'等。不筛选时不传")
                                                                                                .build(),
                                                                                "level", Property.builder()
                                                                                                .type("number")
                                                                                                .description("客户等级：1=普通, 2=VIP, 3=钻石。不筛选时不传")
                                                                                                .build(),
                                                                                "limit", Property.builder()
                                                                                                .type("number")
                                                                                                .description("返回数量限制，默认10，最大50")
                                                                                                .build()))
                                                                .required(List.of())
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 创建获取文件总数的函数定义
         */
        public static FunctionDefinition getFileCount() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("get_file_count")
                                                .description("获取知识库中文件/资料的总数量。当用户询问'有多少文件'、'文件数量'、'上传了多少资料'等问题时调用。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of())
                                                                .required(List.of())
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 创建获取文件列表的函数定义
         */
        public static FunctionDefinition getFileList() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("get_file_list")
                                                .description("获取知识库中的文件/资料列表。当用户想要查看具体的文件名、资料清单、都有哪些文件等时调用。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of(
                                                                                "limit", Property.builder()
                                                                                                .type("number")
                                                                                                .description("返回数量限制，默认20，最大100")
                                                                                                .build()))
                                                                .required(List.of())
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 创建获取文件详情的函数定义
         */
        public static FunctionDefinition getFileDetail() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("get_file_detail")
                                                .description("获取指定文件的详细内容。当用户想要查看某个文件的内容、文件详情时调用。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of(
                                                                                "fileName", Property.builder()
                                                                                                .type("string")
                                                                                                .description("文件名称或标题")
                                                                                                .build()))
                                                                .required(List.of("fileName"))
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 创建Web搜索的函数定义
         */
        public static FunctionDefinition searchWeb() {
                return FunctionDefinition.builder()
                                .type("function")
                                .function(Function.builder()
                                                .name("search_web")
                                                .description("搜索互联网获取信息。当用户询问系统内部没有的信息、名人、公司、时事新闻、外部实体等信息时调用。例如：'搜索西安植物园郑园长'、'查一下最新的农业政策'。")
                                                .parameters(Parameters.builder()
                                                                .type("object")
                                                                .properties(Map.of(
                                                                                "query", Property.builder()
                                                                                                .type("string")
                                                                                                .description("搜索关键词")
                                                                                                .build()))
                                                                .required(List.of("query"))
                                                                .build())
                                                .build())
                                .build();
        }

        /**
         * 获取所有可用的函数定义
         */
        public static List<FunctionDefinition> getAllFunctions() {
                return List.of(
                                getCustomerCount(),
                                getCustomerList(),
                                getFileCount(),
                                getFileList(),
                                getFileDetail(),
                                searchWeb());
        }
}
