package com.connorjensen.griddlers;

// Formatter fixture: scrambled groups, duplicate/unused imports, and a wildcard.
// After formatting: static, java/javax, third-party, then project imports.
import tools.jackson.databind.node.ObjectNode;
import java.util.TreeMap;
import static java.lang.Math.max;
import com.connorjensen.griddlers.testpackage.TestEnum;


import java.util.List;
import static com.connorjensen.griddlers.testpackage.TestEnum.YELLOW;
import java.io.IOException;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static java.lang.Math.abs;
import java.lang.String;
import java.util.List;


import static java.lang.Math.min;

// Keep this original messy; copy both fixtures into src/main/java to experiment.
// Requires wildcard expansion, unused-import removal, and custom import ordering.
public final class ImportOrderTest
{
    private ImportOrderTest () {}

    public static void main (String [] args)
    {
        List <TestEnum> colors=new ArrayList <>(List.of(TestEnum.BLUE,YELLOW,TestEnum.RED));
        Map <String,Integer> scores=new TreeMap <>();
        int warmCount=0; int score=abs(-3);

        for (TestEnum color:colors)
        {
                if (color.isWarm()) {
                    warmCount++;
                    scores.put(color.label(),max(score,5));
                }
                else
                {
                    scores.put(color.label(),score);
                }
        }

        JsonMapper mapper=JsonMapper.builder().build();
        ObjectNode summary=mapper.createObjectNode();
        summary.put("colorCount",colors.size()); summary.put("warmCount",warmCount);
        for (Map.Entry <String,Integer> entry:scores.entrySet()) {
            summary.put(entry.getKey(),entry.getValue());
        }

        boolean hasCoolColors= ! (warmCount==colors.size()) ;
        summary.put("hasCoolColors",hasCoolColors);
        String description="This deliberately long string exercises automatic string wrapping while the color summary exercises collection formatting, operators, method calls, and import ordering.";
        System.out.println(description); System.out.println(summary);
    }

}
