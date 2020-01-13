package com.sp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by admin on 2019/12/24.
 */
public class PatternUtil {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("输入参数错误");
            System.exit(1);
        }

        String patternStr = args[0];// 模式
        String inputStr = "北京 杭州 杭州 北京";//args[1]; // 检验字符串

        System.out.println(checkMatch(patternStr, inputStr));
    }

    /**
     * 匹配模式 检验字符串
     * @param patternStr
     * @param inputStr
     *
     * @ return true 符合， false 不符合
     */
    private static boolean checkMatch(String patternStr, String inputStr) {
        final String regexAnyWithoutSpace = "([^\\s]+)";
        final String regexSpace = "\\s";

        // key 为字符，value 为反向引用序号，用于构造正则表达式
        Map<String, Integer> regexPlaceholderIndexMap = new HashMap<>();
        // 正则中，反向引用序号从 1 开始
        Integer regexPlaceholderIndex = 1;
        // 生成正则表达式
        List<String> regexPatternArray = new ArrayList<>();

        Integer patternLength = patternStr.length();
        for (Integer index = 0; index < patternLength; index++) {
            String needle = new String(new char[]{patternStr.charAt(index)});
            Integer currentPlaceHolderIndex = regexPlaceholderIndexMap.get(needle);
            if (currentPlaceHolderIndex == null) {
                // 如果为空，证明这一字符刚刚出现
                regexPatternArray.add(regexAnyWithoutSpace);
                regexPlaceholderIndexMap.put(needle, regexPlaceholderIndex);
                regexPlaceholderIndex++;
            } else {
                // 如果不为空，则证明为先前的某一字符，需用反向引用方式指代
                regexPatternArray.add(String.format("\\%d", currentPlaceHolderIndex));
            }
        }
        // 将正则各个部分用空格串起来，与需求匹配
        String regex = String.format("^%s$", String.join(regexSpace, regexPatternArray));
        System.out.println("regex=" + regex);
        Pattern p = Pattern.compile(regex);
        return p.matcher(inputStr).find();
    }
}
