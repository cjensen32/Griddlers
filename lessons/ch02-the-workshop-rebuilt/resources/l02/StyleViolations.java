// Deliberately invalid style; valid Java. for ./Griddlers/lessons/ch02-the-workshop-rebuilt/02-the-quality-gates.md
package Com.connorjensen.griddlers.BadPackage;

import java.util.Set;
import java.util.List;
import java.lang.String;
import java.util.*;
import static java.lang.Math.abs;

// TypeName: the class name deliberately starts with a lowercase letter.
class bad_style {
    // MemberName, ConstantName, and Indentation.
    private int BadMember = 1;
    private static final int badConstant = 2;

    // MethodName, ParameterName, MethodParamPad, and LeftCurly.
    void BadMethod (int BadParameter)
    {
        // LocalVariableName and MultipleVariableDeclarations.
        int BadLocal = 1, other = 2;

        // GenericWhitespace: spaces outside the type arguments.
        List <String> labels = new ArrayList <String>();

        // WhitespaceAfter, WhitespaceAround, and OneStatementPerLine.
        int total=abs(BadLocal); total=total+Math.max(BadParameter,other);

        // NoWhitespaceAfter: space after unary !; NoWhitespaceBefore: space before ;.
        boolean ready = ! false ;

        // NeedBraces.
        if (ready)
            total++;

        // EmptyBlock: this conditional body has no statements.
        if (total > 0) {
        }

        // RightCurly: else deliberately starts on a separate line.
        if (ready) {
            labels.add("ready");
        }
        else {
            labels.add("waiting");
        }

        // EmptyCatchBlock: neither handling code nor an explanatory comment in the catch.
        try {
            Integer.parseInt("not a number");
        } catch (NumberFormatException ex) {
        }

        // FallThrough: the first case proceeds into the next case without a break.
        switch (BadParameter) {
            case 0:
                total++;
            case 1:
                total += badConstant;
                break;
            default:
                total += BadMember;
        }

        // LineLength: the following ordinary comment exceeds the configured one hundred character limit by a considerable margin and contains no URL exemption.
        System.out.println(labels.size() + total);
    }

    // EqualsHashCode: equals is overridden, but hashCode is deliberately missing.
    @Override
    public boolean equals(Object other) {
        return this == other;
    }
}

// HideUtilityClassConstructor: static-only class with an implicit accessible constructor.
class UtilityProbe {
  static int twice(int value) {
    return value * 2;
  }
}
