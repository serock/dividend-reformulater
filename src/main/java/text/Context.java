// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Context {

    private static final int COLUMN_SECURITY_DESCRIPTION = 0;
    private static final int COLUMN_TRANSACTION_TYPE = 6;
    private static final int COLUMN_NOTE = 7;
    private static final String[] EMPTY_STRING_ARRAY = new String[] {};

    private final List<List<String>> distributionDetailRows;
    private final List<List<String>> supplementalInfoRows;

    private State state;

    public Context() {
        this.distributionDetailRows = new ArrayList<>();
        this.supplementalInfoRows = new ArrayList<>();
        this.state = new SearchState();
    }

    public void setState(final State newState) {
        this.state = newState;
    }

    public void addDistributionDetailHeaderRowIfNeeded() {
        if (distributionDetailRows().isEmpty()) {
            final List<String> row = new ArrayList<>(8);
            row.add("Security description");
            row.add("CUSIP");
            row.add("Symbol");
            row.add("State");
            row.add("Date");
            row.add("Amount");
            row.add("Transaction type");
            row.add("Notes");
            distributionDetailRows().add(row);
        }
    }

    public void addDistributionDetailRow(final List<String> row) {
        distributionDetailRows().add(row);
    }

    public void addSupplementalInfoHeaderRowIfNeeded() {
        if (supplementalInfoRows().isEmpty()) {
            final String[] headers = new String[] {
                    "Security description",
                    "Source",
                    "State",
                    "Percentage",
                    "Amount"
            };
            supplementalInfoRows().add(Arrays.asList(headers));
        }
    }

    public void addSupplementalInfoRow(final List<String> row) {
        supplementalInfoRows().add(row);
    }

    public List<String> getLastDistributionDetailRow() {
        return distributionDetailRows().get(distributionDetailRows().size() - 1);
    }

    public List<String> getLastSupplementalInfoRow() {
        return supplementalInfoRows().get(supplementalInfoRows().size() - 1);
    }

    public Set<String> getForeignTaxTransactionTypes() {
        final Set<String> transactionTypes = new HashSet<>();
        String transactionType;
        for (List<String> row : distributionDetailRows()) {
            transactionType = row.get(COLUMN_TRANSACTION_TYPE);
            if (transactionType.contains("Foreign tax")) {
                transactionTypes.add(transactionType);
            }
        }
        return transactionTypes;
    }

    public String[][] getForm1099DivFormulas() {
        final List<String[]> formulas = new ArrayList<>();
        formulas.add(new String[] { "'1a-", "Total ordinary dividends (includes lines 1b, 5, 2e)", "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1)" });
        formulas.add(new String[] { "'1b-", "Qualified dividends", "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1; \"Transaction type\"; \"Qualified dividend\")" });
        if (hasLongTermCapitalGain()) {
            formulas.add(new String[] { "'2a-", "Total capital gain distributions (includes lines 2b, 2c, 2d, 2f)",
                    "=SUMIF($'dividend-detail'.G:G; \"Long-term capital gain\"; $'dividend-detail'.F:F)+SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)" });
        }
        if (hasUnrecapturedSection1250Gain()) {
            formulas.add(new String[] { "'2b-", "Unrecaptured Section 1250 gain", "=SUMIF($'dividend-detail'.G:G; \"Unrecaptured section 1250 gain\"; $'dividend-detail'.F:F)" });
        }
        if (hasNondividendDistribution()) {
            formulas.add(new String[] { "'3-", "Nondividend distributions", "=GETPIVOTDATA(\"Amount\"; $'nondividend-distributions'.$A$1; \"Transaction type\"; \"Nondividend distribution\")" });
        }
        if (hasSection199aDividend()) {
            formulas.add(new String[] { "'5-", "Section 199A dividends", "=GETPIVOTDATA(\"Amount\"; $'ordinary-dividends'.$A$1; \"Transaction type\"; \"Section 199A dividend\")" });
        }
        if (hasForeignTaxPaid()) {
            formulas.add(new String[] { "'7-", "Foreign tax paid", "=ABS(GETPIVOTDATA(\"Amount\"; $'foreign-tax-paid'.$A$1))" });
        }
        if (hasTaxExemptDividends()) {
            formulas.add(new String[] { "'12-", "Exempt-interest dividends (includes line 13)", "=GETPIVOTDATA(\"Amount\"; $'tax-exempt-dividends'.$A$1)" });
        }
        return formulas.toArray(new String[0][]);
    }

    public String[][] getDividendDetailFormulas() {
        return getFormulas(distributionDetailRows());
    }

    public String getSecurityDescriptionForNote(final String note) {
        final String noteFormula = '\'' + note;
        final List<List<String>> rows = distributionDetailRows();
        String securityDescription = "";
        for (List<String> row : rows) {
            if (noteFormula.equals(row.get(COLUMN_NOTE))) {
                securityDescription = row.get(COLUMN_SECURITY_DESCRIPTION);
                break;
            }
        }
        return securityDescription;
    }

    public String[][] getSupplementalInfoFormulas() {
        return getFormulas(supplementalInfoRows());
    }

    public int getSupplementalInfoSize() {
        return supplementalInfoRows().size();
    }

    public boolean hasForeignTaxPaid() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).startsWith("Foreign tax")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLongTermCapitalGain() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).equals("Long-term capital gain")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNondividendDistribution() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).equals("Nondividend distribution")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSection199aDividend() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).equals("Section 199A dividend")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSupplementalInfo() {
        return !supplementalInfoRows().isEmpty();
    }

    public boolean hasTaxExemptDividends() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).equals("Tax-exempt dividend")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUnrecapturedSection1250Gain() {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(COLUMN_TRANSACTION_TYPE).equals("Unrecaptured section 1250 gain")) {
                return true;
            }
        }
        return false;
    }

    public void removeLastSupplementalInfoRow() {
        supplementalInfoRows().remove(supplementalInfoRows().size() - 1);
    }

    public State state() {
        return this.state;
    }

    private static String[][] getFormulas(final List<List<String>> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (List<String> row : rows) {
            formulas[rowIndex++] = row.toArray(EMPTY_STRING_ARRAY);
        }
        return formulas;
    }

    private List<List<String>> distributionDetailRows() {
        return this.distributionDetailRows;
    }

    private List<List<String>> supplementalInfoRows() {
        return this.supplementalInfoRows;
    }
}
