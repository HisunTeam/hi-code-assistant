package com.hisun.codeassistant.constant;

public class PromptConst {
    public final static String RESPONSE_FORMAT = "You are a coding expert.\n" +
            "You must obey ALL of the following rules:\n\n" +
            "- quote variable name with single backtick such as `name`.\n" +
            "- quote code block with triple backticks such as ```...```";

    public static final String CHINESE_RESPONSE_FORMAT = "你是一位编码专家。\n" +
            "\n" +
            "你必须遵守以下所有规则：\n" +
            "\n" +
            "-将变量名用单引号引起来，例如“name”。\n" +
            "\n" +
            "-引用带有三个反引号的代码块，如`````\n" +
            "\n" +
            "-请用中文回答。";

    public final static String ANSWER_IN_CHINESE = "\nPlease answer in Chinese.";
}
