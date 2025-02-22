// SPDX-License-Identifier: MIT
package text;

class Form1099DivState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (context.hasNoForm1099DivRows()) {
            String[] row;
            row = new String[] {
                    "'1a-",
                    "Total ordinary dividends (includes lines 1b, 5, 2e)",
                    "=GETPIVOTDATA(\"Amount\"; $'ordinary'.$A$1)"
            };
            context.addForm1099DivRow(row);

            row = new String[] {
                    "'1b-",
                    "Qualified dividends",
                    "=GETPIVOTDATA(\"Amount\"; $'ordinary'.$A$1; \"Transaction type\"; \"Qualified dividend\")"
            };
            context.addForm1099DivRow(row);

            if (context.hasLongTermCapitalGain()) {
                row = new String[] {
                        "'2a-",
                        "Total capital gain distributions (includes lines 2b, 2c, 2d, 2f)",
                        "=SUMIF($'dividend-detail'.G:G; \"Long-term capital gain\"; $'dividend-detail'.F:F)+SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)"
                };
                context.addForm1099DivRow(row);
            }
            if (context.hasUnrecapturedSection1250Gain()) {
                row = new String[] {
                        "'2b-",
                        "Unrecaptured Section 1250 gain",
                        "=SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)"
                };
                context.addForm1099DivRow(row);
            }
            if (context.hasNondividendDistribution()) {
                row = new String[] {
                        "'3-",
                        "Nondividend distributions",
                        "=GETPIVOTDATA(\"Amount\"; $'nondividend-distributions'.$A$1; \"Transaction type\"; \"Nondividend distribution\")"
                };
                context.addForm1099DivRow(row);
            }
            if (context.hasSection199aDividend()) {
                row = new String[] {
                        "'5-",
                        "Section 199A dividends",
                        "=GETPIVOTDATA(\"Amount\"; $'ordinary'.$A$1; \"Transaction type\"; \"Section 199A dividend\")"
                };
                context.addForm1099DivRow(row);
            }
            if (context.hasForeignTaxPaid()) {
                row = new String[] {
                        "'7-",
                        "Foreign tax paid",
                        "=ABS(GETPIVOTDATA(\"Amount\"; $'foreign-tax-paid'.$A$1))"
                };
                context.addForm1099DivRow(row);
            }
            if (context.hasTaxExemptDividend()) {
                row = new String[] {
                        "'12-",
                        "Exempt-interest dividends (includes line 13)",
                        "=GETPIVOTDATA(\"Amount\"; $'tax-exempt'.$A$1)"
                };
                context.addForm1099DivRow(row);
            }
        }
    }
}
