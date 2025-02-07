// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupplementalInfoState implements State {

    private static final Pattern patternEndWithCusipAndSymbol = Pattern.compile("/\\s+[A-Z0-9]{7,9}\\s+/\\s+[A-Z]{1,5}$"); // end with cusip, symbol
    private static final Pattern patternFgnSourceIncAdj = Pattern.compile("Fgn Source Inc Adj");
    private static final Pattern patternFgnSourceIncQual = Pattern.compile("Fgn Source Inc Qual");
    private static final Pattern patternPercent = Pattern.compile("[0-9]+\\.[0-9]{2}%");
    private static final Pattern patternStartsWithFedSourceTotal = Pattern.compile("^Fed Source Total");
    private static final Pattern patternStartsWithFgnSourceIncTot = Pattern.compile("^Fgn Source Inc Tot");
    private static final Pattern patternUSGOPercentage = Pattern.compile("the U.S government obligation percentage is");
    private static final Pattern patternWhitespace = Pattern.compile("\\s+"); // whitespace

    private static final int COLUMN_SECURITY_DESCRIPTION = 0;
    private static final int COLUMN_SOURCE = 1;
    private static final int COLUMN_PERCENTAGE = 2;

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page ")) {
            context.setState(new SearchState());
            return;
        }
        Matcher matcher;
        matcher = patternEndWithCusipAndSymbol.matcher(text);
        if (matcher.find()) {
            context.addSupplementalInfoHeaderRowIfNeeded();
            final String securityDescription = String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim()));
            final List<String> row = createBlankRow();
            row.set(COLUMN_SECURITY_DESCRIPTION, securityDescription);
            context.addSupplementalInfoRow(row);
            return;
        }
        if (text.startsWith("TAX-EXEMPT INTEREST DIVIDENDS")) {
            context.removeLastSupplementalInfoRow();
            return;
        }
        matcher = patternStartsWithFedSourceTotal.matcher(text);
        if (matcher.find()) {
            final int end = matcher.end();
            final List<String> lastRow = context.getLastSupplementalInfoRow();
            if (lastRow.get(COLUMN_SOURCE).equals("")) {
                lastRow.set(COLUMN_SOURCE, "Fed Source Total");
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                lastRow.set(COLUMN_PERCENTAGE, percent);
            } else {
                final List<String> row = createBlankRow();
                row.set(COLUMN_SECURITY_DESCRIPTION, lastRow.get(COLUMN_SECURITY_DESCRIPTION));
                row.set(COLUMN_SOURCE, "Fed Source Total");
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row.set(COLUMN_PERCENTAGE, percent);
                context.addSupplementalInfoRow(row);
            }
            return;
        }
        matcher = patternStartsWithFgnSourceIncTot.matcher(text);
        if (matcher.find()) {
            int end = matcher.end();
            List<String> lastRow = context.getLastSupplementalInfoRow();
            if (lastRow.get(COLUMN_SOURCE).equals("")) {
                lastRow.set(COLUMN_SOURCE, "Fgn Source Inc Tot");
                matcher = patternPercent.matcher(text);
                String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                lastRow.set(COLUMN_PERCENTAGE, percent);
            } else {
                final List<String> row = createBlankRow();
                row.set(COLUMN_SECURITY_DESCRIPTION, lastRow.get(COLUMN_SECURITY_DESCRIPTION));
                row.set(COLUMN_SOURCE, "Fgn Source Inc Tot");
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row.set(COLUMN_PERCENTAGE, percent);
                context.addSupplementalInfoRow(row);
            }
            matcher = patternFgnSourceIncQual.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final List<String> row = createBlankRow();
                row.set(COLUMN_SECURITY_DESCRIPTION, lastRow.get(COLUMN_SECURITY_DESCRIPTION));
                row.set(COLUMN_SOURCE, "Fgn Source Inc Qual");
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row.set(COLUMN_PERCENTAGE, percent);
                context.addSupplementalInfoRow(row);
            }
            matcher = patternFgnSourceIncAdj.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final List<String> row = createBlankRow();
                row.set(COLUMN_SECURITY_DESCRIPTION, lastRow.get(COLUMN_SECURITY_DESCRIPTION));
                row.set(COLUMN_SOURCE, "Fgn Source Inc Adj");
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row.set(COLUMN_PERCENTAGE, percent);
                context.addSupplementalInfoRow(row);
            }
            return;
        }
        matcher = patternUSGOPercentage.matcher(text);
        if (matcher.find()) {
            final String note = text.substring(0, 2);
            final int end = matcher.end();
            final List<String> row = createBlankRow();
            row.set(COLUMN_SECURITY_DESCRIPTION, context.getSecurityDescriptionForNote(note));
            row.set(COLUMN_SOURCE, "Fed Source Total");
            matcher = patternPercent.matcher(text);
            final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
            row.set(COLUMN_PERCENTAGE, percent);
            context.addSupplementalInfoRow(row);
            return;
        }
    }

    private static List<String> createBlankRow() {
        final int capacity = 3;
        final List<String> row = new ArrayList<>(capacity);
        for (int i = capacity; i > 0; i--) {
            row.add("");
        }
        return row;
    }

    private static String cleanPercent(final String percent) {
        final StringBuilder result = new StringBuilder();
        result.append(percent);
        if (percent.endsWith("%")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.deleteCharAt(result.length() - 3);
        while (result.length() < 5) {
            result.insert(0, '0');
        }
        result.insert(1, '.');
        return result.toString();
    }
}
