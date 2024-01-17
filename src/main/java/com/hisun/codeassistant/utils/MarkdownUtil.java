package com.hisun.codeassistant.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.codeassistant.toolwindows.chat.ResponseNodeRenderer;
import com.hisun.codeassistant.toolwindows.components.TextContentRenderer;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class MarkdownUtil {
    private static final Map<String, String> languageFileExtMap = buildLanguageFileExtMap();
    private static final String DEFAULT_CODE_FILE_EXTENSION = ".java";

    /**
     *  Splits a given string into a list of strings where each element is either a code block
     *  surrounded by triple backticks or a non-code block text.
     *
     *  @param inputMarkdown The input markdown formatted string to be split.
     *  @return A list of strings where each element is a code block or a non-code block text from the
     *  input string.
     */
    public static List<String> splitCodeBlocks(String inputMarkdown) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?s)```.*?```");
        Matcher matcher = pattern.matcher(inputMarkdown);
        int start = 0;
        while (matcher.find()) {
            result.add(inputMarkdown.substring(start, matcher.start()));
            result.add(matcher.group());
            start = matcher.end();
        }
        result.add(inputMarkdown.substring(start));
        return result.stream().filter(item -> !item.isBlank()).collect(toList());
    }

    public static String convertMdToHtml(String message) {
        MutableDataSet options = new MutableDataSet();
        var document = Parser.builder(options).build().parse(message);
        return HtmlRenderer.builder(options)
                .nodeRendererFactory(new ResponseNodeRenderer.Factory())
                .build()
                .render(document);
    }
    public static String getFileExtensionFromLanguage(String language) {
        return languageFileExtMap.getOrDefault(language.toLowerCase(), DEFAULT_CODE_FILE_EXTENSION);
    }

    private static Map<String, String> buildLanguageFileExtMap() {
        Map<String, String> languageFileExtMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<LanguageFileExtInfo> languageFileExtInfos;
        try {
            URL resource = MarkdownUtil.class.getResource("/languageMappings.json");
            languageFileExtInfos = objectMapper.readValue(resource, new TypeReference<>() {
            });
            for (LanguageFileExtInfo languageFileExtInfo : languageFileExtInfos) {
                languageFileExtMap.put(languageFileExtInfo.getLanguageName().toLowerCase(),
                        languageFileExtInfo.getFileExtensions().get(0));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return languageFileExtMap;
    }

    public static List<String> divideMarkdown(String markdownContent) {
        List<String> blocks = new ArrayList<>();
        Pattern codeBlockPattern = Pattern.compile("(?s)```.*?```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(markdownContent);
        int previousEnd = 0;
        while (codeBlockMatcher.find()) {
            blocks.add(markdownContent.substring(previousEnd, codeBlockMatcher.start()));
            blocks.add(codeBlockMatcher.group());
            previousEnd = codeBlockMatcher.end();
        }
        blocks.add(markdownContent.substring(previousEnd));
        return blocks.stream()
                .filter(section -> !section.isBlank())
                .collect(Collectors.toList());
    }
    public static String textContent2Html(String markdownText) {
        MutableDataSet options = new MutableDataSet();
        Document document = Parser.builder(options).build().parse(markdownText);
        return HtmlRenderer.builder(options)
                .nodeRendererFactory(new TextContentRenderer.Factory())
                .build()
                .render(document);
    }

    public static String extractContents(String codeBlock) {
        List<String> blocks = divideMarkdown(codeBlock);
        List<String> contents = new ArrayList<>();
        for (String block : blocks) {
            if (block.startsWith("```")) {
                com.vladsch.flexmark.util.ast.Document parse = Parser.builder().build().parse(codeBlock);
                FencedCodeBlock codeNode = (FencedCodeBlock) parse.getChildOfType(FencedCodeBlock.class);
                if (codeNode == null) {
                    return null;
                }
                contents.add(codeNode.getContentChars().unescape().replaceAll("\\n$", ""));
            }
        }
        if (CollectionUtils.isEmpty(contents)) {
            return codeBlock;
        }
        return StringUtils.join(contents, "\n\n");
    }

    @Setter
    @Getter
    public static class LanguageFileExtInfo {

        private String languageName;

        private List<String> fileExtensions;

    }
}
