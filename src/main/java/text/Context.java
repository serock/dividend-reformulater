// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Context {

    private final List<String[]> distributionDetailRows;
    private final List<String[]> form1099DivRows;
    private final List<String[]> supplementalInfoRows;

    private State state;

    public Context() {
        this.distributionDetailRows = new ArrayList<>();
        this.form1099DivRows = new ArrayList<>();
        this.supplementalInfoRows = new ArrayList<>();
        this.state = new SearchState();
    }

    public String[][] getDividendDetailFormulas() {
        return getFormulas(distributionDetailRows());
    }

    public Set<String> getForeignTaxTransactionTypes() {
        final Set<String> transactionTypes = new HashSet<>();
        String transactionType;
        for (String[] row : distributionDetailRows()) {
            transactionType = row[Constants.DD_FIELD_TRANSACTION_TYPE];
            if (transactionType.contains("Foreign tax")) {
                transactionTypes.add(transactionType);
            }
        }
        return transactionTypes;
    }

    public String[][] getForm1099DivFormulas() {
        return getFormulas(form1099DivRows());
    }

    public String[][] getSupplementalInfoFormulas() {
        return getFormulas(supplementalInfoRows());
    }

    public boolean hasForeignTaxPaid() {
        final List<String[]> rows = distributionDetailRows();
        for (String[] row : rows) {
            if (row[Constants.DD_FIELD_TRANSACTION_TYPE].startsWith("Foreign tax")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNondividendDistribution() {
        return hasMatchingDividendDetail(Constants.DD_FIELD_TRANSACTION_TYPE, "Nondividend distribution");
    }

    public boolean hasSupplementalInfo() {
        return !supplementalInfoRows().isEmpty();
    }

    public boolean hasTaxExemptDividend() {
        return hasMatchingDividendDetail(Constants.DD_FIELD_TRANSACTION_TYPE, "Tax-exempt dividend");
    }

    public void setState(final State newState) {
        this.state = newState;
    }

    public State state() {
        return this.state;
    }

    void addDistributionDetailRow(final String[] row) {
        distributionDetailRows().add(row);
    }

    void addForm1099DivRow(final String[] row) {
        form1099DivRows().add(row);
    }

    void addSupplementalInfoRow(final String[] row) {
        supplementalInfoRows().add(row);
    }

    String[] getLastDistributionDetailRow() {
        return distributionDetailRows().get(distributionDetailRows().size() - 1);
    }

    String[] getLastSupplementalInfoRow() {
        return supplementalInfoRows().get(supplementalInfoRows().size() - 1);
    }

    String getSecurityDescriptionForNote(final String note) {
        final String noteFormula = '\'' + note;
        final List<String[]> rows = distributionDetailRows();
        String securityDescription = "";
        for (String[] row : rows) {
            if (noteFormula.equals(row[Constants.DD_FIELD_NOTES])) {
                securityDescription = row[Constants.DD_FIELD_SECURITY_DESCRIPTION];
                break;
            }
        }
        return securityDescription;
    }

    int getSupplementalInfoSize() {
        return supplementalInfoRows().size();
    }

    boolean hasLongTermCapitalGain() {
        return hasMatchingDividendDetail(Constants.DD_FIELD_TRANSACTION_TYPE, "Long-term capital gain");
    }

    boolean hasNoDistributionDetail() {
        return distributionDetailRows().isEmpty();
    }

    boolean hasNoForm1099DivRows() {
        return form1099DivRows().isEmpty();
    }

    boolean hasNoSupplementalInfo() {
        return supplementalInfoRows().isEmpty();
    }

    boolean hasSection199aDividend() {
        return hasMatchingDividendDetail(Constants.DD_FIELD_TRANSACTION_TYPE, "Section 199A dividend");
    }

    boolean hasUnrecapturedSection1250Gain() {
        return hasMatchingDividendDetail(Constants.DD_FIELD_TRANSACTION_TYPE, "Unrecaptured section 1250 gain");
    }

    void removeLastSupplementalInfoRow() {
        supplementalInfoRows().remove(supplementalInfoRows().size() - 1);
    }

    private static String[][] getFormulas(final List<String[]> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (String[] row : rows) {
            formulas[rowIndex++] = row;
        }
        return formulas;
    }

    private boolean hasMatchingDividendDetail(final int field, final String value) {
        final List<String[]> rows = distributionDetailRows();
        for (String[] row : rows) {
            if (row[field].equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<String[]> distributionDetailRows() {
        return this.distributionDetailRows;
    }

    private List<String[]> form1099DivRows() {
        return this.form1099DivRows;
    }

    private List<String[]> supplementalInfoRows() {
        return this.supplementalInfoRows;
    }
}
