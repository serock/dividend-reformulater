package text;

import java.util.ArrayList;
import java.util.List;

public class Form1099DivState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (context.hasNoForm1099DivRows()) {
            List<String> row;
            row = createBlankRow();
            row.set(0, "'1a-");
            row.set(1, "Total ordinary dividends (includes lines 1b, 5, 2e)");
            row.set(2, "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1)");
            context.addForm1099DivRow(row);

            row = createBlankRow();
            row.set(0, "'1b-");
            row.set(1, "Qualified dividends");
            row.set(2, "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1; \"Transaction type\"; \"Qualified dividend\")");
            context.addForm1099DivRow(row);

            if (context.hasLongTermCapitalGain()) {
                row = createBlankRow();
                row.set(0, "'2a-");
                row.set(1, "Total capital gain distributions (includes lines 2b, 2c, 2d, 2f)");
                row.set(2, "=SUMIF($'dividend-detail'.G:G; \"Long-term capital gain\"; $'dividend-detail'.F:F)+SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)");
                context.addForm1099DivRow(row);
            }
            if (context.hasUnrecapturedSection1250Gain()) {
                row = createBlankRow();
                row.set(0, "'2b-");
                row.set(1, "Unrecaptured Section 1250 gain");
                row.set(2, "=SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)");
                context.addForm1099DivRow(row);
            }
            if (context.hasNondividendDistribution()) {
                row = createBlankRow();
                row.set(0, "'3-");
                row.set(1, "Nondividend distributions");
                row.set(2, "=GETPIVOTDATA(\"Amount\"; $'nondividend-distributions'.$A$1; \"Transaction type\"; \"Nondividend distribution\")");
                context.addForm1099DivRow(row);
            }
            if (context.hasSection199aDividend()) {
                row = createBlankRow();
                row.set(0, "'5-");
                row.set(1, "Section 199A dividends");
                row.set(2, "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1; \"Transaction type\"; \"Section 199A dividend\")");
                context.addForm1099DivRow(row);
            }
            if (context.hasForeignTaxPaid()) {
                row = createBlankRow();
                row.set(0, "'7-");
                row.set(1, "Foreign tax paid");
                row.set(2, "=ABS(GETPIVOTDATA(\"Amount\"; $'foreign-tax-paid'.$A$1))");
                context.addForm1099DivRow(row);
            }
            if (context.hasTaxExemptDividend()) {
                row = createBlankRow();
                row.set(0, "'12-");
                row.set(1, "Exempt-interest dividends (includes line 13)");
                row.set(2, "=GETPIVOTDATA(\"Amount\"; $'tax-exempt-dividends'.$A$1)");
                context.addForm1099DivRow(row);
            }
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
}
