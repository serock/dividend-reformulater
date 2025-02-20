// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistributionDetailDataState implements State {

    private static final Pattern patternDateAmount = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{2}\\s+[-0-9,]+\\.[0-9]{2}");
    private static final Pattern patternStartWithNoteDateAmount = Pattern.compile("^Note:\\s+[0-9]{2}\\s+[0-9]{2}/[0-9]{2}/[0-9]{2}\\s+[-0-9,]+\\.[0-9]{2}");
    private static final Pattern patternEndWithCusipSymbolState = Pattern.compile("[A-Z0-9]{7,9}\\s+[A-Z/]{1,5}\\s+[A-Z]{2}$");
    private static final Pattern patternEndWithCusipSymbol = Pattern.compile("[A-Z0-9]{7,9}\\s+[A-Z/]{1,5}$");
    private static final Pattern patternEndWithCusip = Pattern.compile("[A-Z0-9]{7,9}$");
    private static final Pattern patternEndWithNote = Pattern.compile("[0-9]{2}$");
    private static final Pattern patternWhitespace = Pattern.compile("\\s+");

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page ")) {
            context.setState(new SearchState());
            return;
        }
        Matcher matcher;
        matcher = patternStartWithNoteDateAmount.matcher(text);
        if (matcher.find()) {
            final String[] row = createBlankRow();
            final String[] lastRow = context.getLastDistributionDetailRow();
            copyBase(lastRow, row);
            extractNoteDateAmount(matcher.group(), row);
            extractTransactionTypeAndNotes(text.substring(matcher.end()).trim(), row);
            context.addDistributionDetailRow(row);
            return;
        }
        matcher = patternDateAmount.matcher(text);
        if (matcher.find()) {
            final String[] row = createBlankRow();
            if (matcher.start() == 0 || isContinuation(text.substring(0, matcher.start()).trim())) {
                final String[] lastRow = context.getLastDistributionDetailRow();
                copyBase(lastRow, row);
            } else {
                extractBase(text.substring(0, matcher.start()).trim(), row);
            }
            extractDateAndAmount(matcher.group(), row);
            extractTransactionTypeAndNotes(text.substring(matcher.end()).trim(), row);
            context.addDistributionDetailRow(row);
            return;
        }
    }

    private static String[] createBlankRow() {
        final int capacity = 8;
        final String[] row = new String[capacity];
        for (int i = capacity - 1; i >= 0; i--) {
            row[i] = "";
        }
        return row;
    }

    private static void copyBase(final String[] fromRow, final String[] toRow) {
        toRow[Constants.DD_FIELD_SECURITY_DESCRIPTION] = fromRow[Constants.DD_FIELD_SECURITY_DESCRIPTION];
        toRow[Constants.DD_FIELD_CUSIP] = fromRow[Constants.DD_FIELD_CUSIP];
        toRow[Constants.DD_FIELD_SYMBOL] = fromRow[Constants.DD_FIELD_SYMBOL];
        toRow[Constants.DD_FIELD_STATE] = fromRow[Constants.DD_FIELD_STATE];
    }

    private static void extractBase(final String text, final String[] row) {
        Matcher matcher = patternEndWithCusipSymbolState.matcher(text);
        if (matcher.find()) {
            final String[] fields = patternWhitespace.split(matcher.group());
            row[Constants.DD_FIELD_SECURITY_DESCRIPTION] = String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim()));
            row[Constants.DD_FIELD_CUSIP] = '\'' + fields[0];
            row[Constants.DD_FIELD_SYMBOL] = fields[1];
            row[Constants.DD_FIELD_STATE] = fields[2];
            return;
        }
        matcher = patternEndWithCusipSymbol.matcher(text);
        if (matcher.find()) {
            final String[] fields = patternWhitespace.split(matcher.group());
            row[Constants.DD_FIELD_SECURITY_DESCRIPTION] = String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim()));
            row[Constants.DD_FIELD_CUSIP] ='\'' + fields[0];
            row[Constants.DD_FIELD_SYMBOL] = fields[1];
            return;
        }
        matcher = patternEndWithCusip.matcher(text);
        if (matcher.find()) {
            row[Constants.DD_FIELD_SECURITY_DESCRIPTION] = String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim()));
            row[Constants.DD_FIELD_CUSIP] = '\'' + matcher.group();
            return;
        }
    }

    private static void extractDateAndAmount(final String text, final String[] row) {
        final String[] fields = patternWhitespace.split(text);
        row[Constants.DD_FIELD_DATE] = fields[0];
        row[Constants.DD_FIELD_AMOUNT] = fields[1];
    }

    private static void extractTransactionTypeAndNotes(final String text, final String[] row) {
        final Matcher matcher = patternEndWithNote.matcher(text);
        if (matcher.find()) {
            row[Constants.DD_FIELD_TRANSACTION_TYPE] = text.substring(0, matcher.start()).trim();
            row[Constants.DD_FIELD_NOTES] = '\'' + text.substring(matcher.start());
        } else {
            row[Constants.DD_FIELD_TRANSACTION_TYPE] = text.trim();
        }
    }

    private static void extractNoteDateAmount(final String text, final String[] row) {
        final String[] fields = patternWhitespace.split(text);
        row[Constants.DD_FIELD_NOTES] ='\'' + fields[1];
        row[Constants.DD_FIELD_DATE] = fields[2];
        row[Constants.DD_FIELD_AMOUNT] = fields[3];
    }

    private static boolean isContinuation(final String text) {
        return text.contains("(cont'd)");
    }
}
