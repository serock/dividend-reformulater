// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupplementalInfoState implements State {

    private static final Pattern patternEndsWithCusipAndSymbol = Pattern.compile("/\\s+[A-Z0-9]{7,9}\\s+/\\s+[A-Z]{1,5}$"); // end with cusip, symbol
    private static final Pattern patternFgnSourceIncAdj = Pattern.compile("Fgn Source Inc Adj");
    private static final Pattern patternFgnSourceIncQual = Pattern.compile("Fgn Source Inc Qual");
    private static final Pattern patternPercent = Pattern.compile("[0-9]+\\.[0-9]{2}%");
    private static final Pattern patternStartsWithFedSourceTotal = Pattern.compile("^Fed Source Total");
    private static final Pattern patternStartsWithFgnSourceIncTot = Pattern.compile("^Fgn Source Inc Tot");
    private static final Pattern patternUSGOPercentage = Pattern.compile("the U.S government obligation percentage is");
    private static final Pattern patternWhitespace = Pattern.compile("\\s+"); // whitespace

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page ")) {
            context.setState(new SearchState());
            return;
        }
        Matcher matcher;
        matcher = patternEndsWithCusipAndSymbol.matcher(text);
        if (matcher.find()) {
            if (context.hasNoSupplementalInfo()) {
                final String[] headers = new String[] {
                        "Security description",
                        "Source",
                        "State",
                        "Percentage",
                        "Amount"
                };
                context.addSupplementalInfoRow(headers);
            }
            final String securityDescription = String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim()));
            final String[] row = createBlankRow();
            row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = securityDescription;
            addSupplementalInfoRow(context, row);
            return;
        }
        if (text.startsWith("TAX-EXEMPT INTEREST DIVIDENDS")) {
            context.removeLastSupplementalInfoRow();
            return;
        }
        matcher = patternStartsWithFedSourceTotal.matcher(text);
        if (matcher.find()) {
            final int end = matcher.end();
            final String[] lastRow = context.getLastSupplementalInfoRow();
            if (lastRow[Constants.SI_FIELD_SOURCE].isEmpty()) {
                lastRow[Constants.SI_FIELD_SOURCE] = "Fed Source Total";
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                lastRow[Constants.SI_FIELD_PERCENTAGE] = percent;
            } else {
                final String[] row = createBlankRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fed Source Total";
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                addSupplementalInfoRow(context, row);
            }
            return;
        }
        matcher = patternStartsWithFgnSourceIncTot.matcher(text);
        if (matcher.find()) {
            int end = matcher.end();
            String[] lastRow = context.getLastSupplementalInfoRow();
            if (lastRow[Constants.SI_FIELD_SOURCE].isEmpty()) {
                lastRow[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Tot";
                matcher = patternPercent.matcher(text);
                String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                lastRow[Constants.SI_FIELD_PERCENTAGE] = percent;
            } else {
                final String[] row = createBlankRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Tot";
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                addSupplementalInfoRow(context, row);
            }
            matcher = patternFgnSourceIncQual.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final String[] row = createBlankRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Qual";
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                addSupplementalInfoRow(context, row);
            }
            matcher = patternFgnSourceIncAdj.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final String[] row = createBlankRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Adj";
                matcher = patternPercent.matcher(text);
                final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                addSupplementalInfoRow(context, row);
            }
            return;
        }
        matcher = patternUSGOPercentage.matcher(text);
        if (matcher.find()) {
            final String note = text.substring(0, 2);
            final int end = matcher.end();
            final String[] row = createBlankRow();
            row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = context.getSecurityDescriptionForNote(note);
            row[Constants.SI_FIELD_SOURCE] = "Fed Source Total";
            matcher = patternPercent.matcher(text);
            final String percent = matcher.find(end) ? cleanPercent(matcher.group()) : "";
            row[Constants.SI_FIELD_PERCENTAGE] = percent;
            addSupplementalInfoRow(context, row);
            return;
        }
    }

    private static void addSupplementalInfoRow(final Context context, final String[] row) {
        final int rowNum = context.getSupplementalInfoSize() + 1;
        row[Constants.SI_FIELD_AMOUNT] = "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1; $A$1; A" + Integer.toString(rowNum) + ")*D" + Integer.toString(rowNum);
        context.addSupplementalInfoRow(row);
    }

    private static String[] createBlankRow() {
        final int capacity = 5;
        final String[] row = new String[capacity];
        for (int i = capacity - 1; i >= 0; i--) {
            row[i] = "";
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
