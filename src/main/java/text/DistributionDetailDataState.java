// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DistributionDetailDataState implements State {

    private static final Pattern patternDateAmount = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{2}\\s+[-0-9,]+\\.[0-9]{2}");
    private static final Pattern patternStartWithNoteDateAmount = Pattern.compile("^Note:\\s+[0-9]{2}\\s+[0-9]{2}/[0-9]{2}/[0-9]{2}\\s+[-0-9,]+\\.[0-9]{2}");
    private static final Pattern patternEndWithCusipSymbolState = Pattern.compile("[A-Z0-9]{7,9}\\s+[A-Z]{1,5}\\s+[A-Z]{2}$");
    private static final Pattern patternEndWithCusipSymbol = Pattern.compile("[A-Z0-9]{7,9}\\s+[A-Z]{1,5}$");
    private static final Pattern patternEndWithCusip = Pattern.compile("[A-Z0-9]{7,9}$");
    private static final Pattern patternEndWithNote = Pattern.compile("[0-9]{2}$");
    private static final Pattern patternWhitespace = Pattern.compile("\\s+");

    private static final int COLUMN_SECURITY_DESCRIPTION = 0;
    private static final int COLUMN_CUSIP = 1;
    private static final int COLUMN_SYMBOL = 2;
    private static final int COLUMN_STATE = 3;
    private static final int COLUMN_DATE = 4;
    private static final int COLUMN_AMOUNT = 5;
    private static final int COLUMN_TRANSACTION_TYPE = 6;
    private static final int COLUMN_NOTES = 7;

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page ")) {
            context.setState(new SearchState());
            return;
        }
        Matcher matcher;
        matcher = patternStartWithNoteDateAmount.matcher(text);
        if (matcher.find()) {
            final List<String> row = createBlankRow();
            final List<String> lastRow = context.getLastDistributionDetailRow();
            copyBase(lastRow, row);
            extractNoteDateAmount(matcher.group(), row);
            extractTransactionTypeAndNotes(text.substring(matcher.end()).trim(), row);
            context.addDistributionDetailRow(row);
            return;
        }
        matcher = patternDateAmount.matcher(text);
        if (matcher.find()) {
            final List<String> row = createBlankRow();
            if (matcher.start() == 0 || isContinuation(text.substring(0, matcher.start()).trim())) {
                final List<String> lastRow = context.getLastDistributionDetailRow();
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

    private static List<String> createBlankRow() {
        final int capacity = 8;
        final List<String> row = new ArrayList<>(capacity);
        for (int i = capacity; i > 0; i--) {
            row.add("");
        }
        return row;
    }

    private static void copyBase(final List<String> fromRow, final List<String> toRow) {
        toRow.set(COLUMN_SECURITY_DESCRIPTION, fromRow.get(COLUMN_SECURITY_DESCRIPTION));
        toRow.set(COLUMN_CUSIP, fromRow.get(COLUMN_CUSIP));
        toRow.set(COLUMN_SYMBOL, fromRow.get(COLUMN_SYMBOL));
        toRow.set(COLUMN_STATE, fromRow.get(COLUMN_STATE));
    }

    private static void extractBase(final String text, final List<String> row) {
        Matcher matcher = patternEndWithCusipSymbolState.matcher(text);
        if (matcher.find()) {
            final String[] fields = patternWhitespace.split(matcher.group());
            row.set(COLUMN_SECURITY_DESCRIPTION, String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim())));
            row.set(COLUMN_CUSIP, fields[0]);
            row.set(COLUMN_SYMBOL, fields[1]);
            row.set(COLUMN_STATE, fields[2]);
            return;
        }
        matcher = patternEndWithCusipSymbol.matcher(text);
        if (matcher.find()) {
            final String[] fields = patternWhitespace.split(matcher.group());
            row.set(COLUMN_SECURITY_DESCRIPTION, String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim())));
            row.set(COLUMN_CUSIP, fields[0]);
            row.set(COLUMN_SYMBOL, fields[1]);
            return;
        }
        matcher = patternEndWithCusip.matcher(text);
        if (matcher.find()) {
            row.set(COLUMN_SECURITY_DESCRIPTION, String.join(" ", patternWhitespace.split(text.substring(0, matcher.start()).trim())));
            row.set(COLUMN_CUSIP, matcher.group());
            return;
        }
    }

    private static void extractDateAndAmount(final String text, final List<String> row) {
        final String[] fields = patternWhitespace.split(text);
        row.set(COLUMN_DATE, fields[0]);
        row.set(COLUMN_AMOUNT, fields[1]);
    }

    private static void extractTransactionTypeAndNotes(final String text, final List<String> row) {
        final Matcher matcher = patternEndWithNote.matcher(text);
        if (matcher.find()) {
            row.set(COLUMN_TRANSACTION_TYPE, text.substring(0, matcher.start()).trim());
            row.set(COLUMN_NOTES, '\'' + text.substring(matcher.start()));
        } else {
            row.set(COLUMN_TRANSACTION_TYPE, text.trim());
        }
    }

    private static void extractNoteDateAmount(String text, List<String> row) {
        final String[] fields = patternWhitespace.split(text);
        row.set(COLUMN_NOTES, '\'' + fields[1]);
        row.set(COLUMN_DATE, fields[2]);
        row.set(COLUMN_AMOUNT, fields[3]);
    }

    private static boolean isContinuation(final String text) {
        return text.contains("(cont'd)");
    }
}
