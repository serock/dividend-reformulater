// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SupplementalInfoState implements State {

    private static final Pattern patternEndsWithCusipAndSymbol = Pattern.compile("/\\s+[A-Z0-9]{7,9}\\s+/\\s+[A-Z]{1,5}$");
    private static final Pattern patternFgnSourceIncAdj = Pattern.compile("Fgn Source Inc Adj");
    private static final Pattern patternFgnSourceIncQual = Pattern.compile("Fgn Source Inc Qual");
    private static final Pattern patternPercent = Pattern.compile("[0-9]{1,3}\\.[0-9]{2}%?");
    private static final Pattern patternStartsWithFedSourceTotal = Pattern.compile("^Fed Source Total");
    private static final Pattern patternStartsWithFgnSourceIncTot = Pattern.compile("^Fgn Source Inc Tot");
    private static final Pattern patternStateAndPercentage = Pattern.compile("([A-Z][A-Za-z. ]{2,16}[a-z])\\s+([0-9]{1,3}\\.[0-9]{2})");
    private static final Pattern patternUSGOPercentage = Pattern.compile("the U.S government obligation percentage is");
    private static final Pattern patternWhitespace = Pattern.compile("\\s+");

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page ")) {
            context.transitionToSearchState();
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
            final String[] row = createEmptyRow();
            row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = securityDescription;
            context.addSupplementalInfoRow(row);
            return;
        }
        if (text.startsWith("PERCENTAGE OF INCOME FROM US GOVERNMENT SECURITIES") || text.startsWith("FOREIGN SOURCE INCOME PERCENTAGES")) {
            String[] lastRow = context.getLastSupplementalInfoRow();
            if (!lastRow[Constants.SI_FIELD_AMOUNT].isEmpty()) {
                final String[] row = createEmptyRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                context.addSupplementalInfoRow(row);
                lastRow = context.getLastSupplementalInfoRow();
            }
            lastRow[Constants.SI_FIELD_AMOUNT] = getOrdinaryDividendsAmountFormula(context);
            return;
        }
        if (text.startsWith("TAX-EXEMPT INTEREST DIVIDENDS")) {
            String[] lastRow = context.getLastSupplementalInfoRow();
            if (!lastRow[Constants.SI_FIELD_AMOUNT].isEmpty()) {
                final String[] row = createEmptyRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                context.addSupplementalInfoRow(row);
                lastRow = context.getLastSupplementalInfoRow();
            }
            lastRow[Constants.SI_FIELD_AMOUNT] = getTaxExemptDividendsAmountFormula(context);
            return;
        }
        matcher = patternStartsWithFedSourceTotal.matcher(text);
        if (matcher.find()) {
            final int end = matcher.end();
            final String[] lastRow = context.getLastSupplementalInfoRow();
            lastRow[Constants.SI_FIELD_SOURCE] = "Fed Source Total";
            matcher = patternPercent.matcher(text);
            final String percent = matcher.find(end) ? matcher.group() : "";
            lastRow[Constants.SI_FIELD_PERCENTAGE] = percent;
            return;
        }
        matcher = patternStartsWithFgnSourceIncTot.matcher(text);
        if (matcher.find()) {
            int end = matcher.end();
            String[] lastRow = context.getLastSupplementalInfoRow();
            lastRow[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Tot";
            matcher = patternPercent.matcher(text);
            String percent = matcher.find(end) ? matcher.group() : "";
            lastRow[Constants.SI_FIELD_PERCENTAGE] = percent;
            matcher = patternFgnSourceIncQual.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final String[] row = createEmptyRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Qual";
                matcher = patternPercent.matcher(text);
                percent = matcher.find(end) ? matcher.group() : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                context.addSupplementalInfoRow(row);
                row[Constants.SI_FIELD_AMOUNT] = getOrdinaryDividendsAmountFormula(context);
            }
            matcher = patternFgnSourceIncAdj.matcher(text);
            if (matcher.find()) {
                end = matcher.end();
                lastRow = context.getLastSupplementalInfoRow();
                final String[] row = createEmptyRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                row[Constants.SI_FIELD_SOURCE] = "Fgn Source Inc Adj";
                matcher = patternPercent.matcher(text);
                percent = matcher.find(end) ? matcher.group() : "";
                row[Constants.SI_FIELD_PERCENTAGE] = percent;
                context.addSupplementalInfoRow(row);
                row[Constants.SI_FIELD_AMOUNT] = getOrdinaryDividendsAmountFormula(context);
            }
            return;
        }
        matcher = patternUSGOPercentage.matcher(text);
        if (matcher.find()) {
            final String note = text.substring(0, 2);
            final int end = matcher.end();
            final String[] row = createEmptyRow();
            row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = context.getSecurityDescriptionForNote(note);
            row[Constants.SI_FIELD_SOURCE] = "Fed Source Total";
            matcher = patternPercent.matcher(text);
            final String percent = matcher.find(end) ? matcher.group() : "";
            row[Constants.SI_FIELD_PERCENTAGE] = percent;
            context.addSupplementalInfoRow(row);
            row[Constants.SI_FIELD_AMOUNT] = getOrdinaryDividendsAmountFormula(context);
            return;
        }
        matcher = patternStateAndPercentage.matcher(text);
        if (matcher.find()) {
            String[] lastRow = context.getLastSupplementalInfoRow();
            if (!lastRow[Constants.SI_FIELD_SOURCE].isEmpty()) {
                return;
            }
            String state;
            String percentage;
            while (true) {
                state = matcher.group(1);
                percentage = matcher.group(2);
                lastRow[Constants.SI_FIELD_STATE] = state;
                lastRow[Constants.SI_FIELD_PERCENTAGE] = percentage + '%';
                if (!matcher.find()) {
                    break;
                }
                final String[] row = createEmptyRow();
                row[Constants.SI_FIELD_SECURITY_DESCRIPTION] = lastRow[Constants.SI_FIELD_SECURITY_DESCRIPTION];
                context.addSupplementalInfoRow(row);
                row[Constants.SI_FIELD_AMOUNT] = getTaxExemptDividendsAmountFormula(context);
                lastRow = row;
            }
        }
    }

    private static String[] createEmptyRow() {
        final int capacity = 5;
        final String[] row = new String[capacity];
        for (int i = capacity - 1; i >= 0; i--) {
            row[i] = "";
        }
        return row;
    }

    private static String getOrdinaryDividendsAmountFormula(final Context context) {
        final int rowNum = context.getSupplementalInfoSize();
        return "=GETPIVOTDATA(\"Amount\"; $'ordinary'.$A$1; $A$1; A" + Integer.toString(rowNum) + ")*D" + Integer.toString(rowNum);
    }

    private static String getTaxExemptDividendsAmountFormula(final Context context) {
        final int rowNum = context.getSupplementalInfoSize();
        return "=GETPIVOTDATA(\"Amount\"; $'tax-exempt'.$A$1; $A$1; A" + Integer.toString(rowNum) + ")*D" + Integer.toString(rowNum);
    }
}
