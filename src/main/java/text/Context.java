// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Context {

    private static final String[] EMPTY_STRING_ARRAY = new String[] {};

    private final List<List<String>> distributionDetailRows;
    private final List<List<String>> form1099DivRows;
    private final List<List<String>> supplementalInfoRows;

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
        for (List<String> row : distributionDetailRows()) {
            transactionType = row.get(Constants.DD_FIELD_TRANSACTION_TYPE);
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
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(Constants.DD_FIELD_TRANSACTION_TYPE).startsWith("Foreign tax")) {
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

    void addDistributionDetailRow(final List<String> row) {
        distributionDetailRows().add(row);
    }

    void addForm1099DivRow(final List<String> row) {
        form1099DivRows().add(row);
    }

    void addSupplementalInfoRow(final List<String> row) {
        supplementalInfoRows().add(row);
    }

    List<String> getLastDistributionDetailRow() {
        return distributionDetailRows().get(distributionDetailRows().size() - 1);
    }

    List<String> getLastSupplementalInfoRow() {
        return supplementalInfoRows().get(supplementalInfoRows().size() - 1);
    }

    String getSecurityDescriptionForNote(final String note) {
        final String noteFormula = '\'' + note;
        final List<List<String>> rows = distributionDetailRows();
        String securityDescription = "";
        for (List<String> row : rows) {
            if (noteFormula.equals(row.get(Constants.DD_FIELD_NOTES))) {
                securityDescription = row.get(Constants.DD_FIELD_SECURITY_DESCRIPTION);
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

    private static String[][] getFormulas(final List<List<String>> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (List<String> row : rows) {
            formulas[rowIndex++] = row.toArray(EMPTY_STRING_ARRAY);
        }
        return formulas;
    }

    private boolean hasMatchingDividendDetail(final int field, final String value) {
        final List<List<String>> rows = distributionDetailRows();
        for (List<String> row : rows) {
            if (row.get(field).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<List<String>> distributionDetailRows() {
        return this.distributionDetailRows;
    }

    private List<List<String>> form1099DivRows() {
        return this.form1099DivRows;
    }

    private List<List<String>> supplementalInfoRows() {
        return this.supplementalInfoRows;
    }
}
