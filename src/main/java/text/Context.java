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

    private final State distributionDetailHeaderState = new DistributionDetailHeaderState();
    private final State distributionDetailDataState = new DistributionDetailDataState();
    private final State form1099DivState = new Form1099DivState();
    private final State searchState = new SearchState();
    private final State supplementalInfoState = new SupplementalInfoState();

    private State state;

    public Context() {
        this.distributionDetailRows = new ArrayList<>();
        this.form1099DivRows = new ArrayList<>();
        this.supplementalInfoRows = new ArrayList<>();
        transitionToSearchState();
    }

    public String[][] getDividendDetailFormulas() {
        return getFormulas(distributionDetailRows());
    }

    public Set<String> getForeignTaxTransactionTypes() {
        final Set<String> transactionTypes = new HashSet<>();
        distributionDetailRows().stream()
            .filter(row -> row[Constants.DD_FIELD_TRANSACTION_TYPE].contains("Foreign tax"))
            .map(row -> row[Constants.DD_FIELD_TRANSACTION_TYPE])
            .distinct()
            .forEach(tt -> transactionTypes.add(tt));
        return transactionTypes;
    }

    public String[][] getForm1099DivFormulas() {
        return getFormulas(form1099DivRows());
    }

    public String[][] getSupplementalInfoFormulas() {
        return getFormulas(supplementalInfoRows());
    }

    public boolean hasForeignTaxPaid() {
        return distributionDetailRows().stream()
                .map(row -> row[Constants.DD_FIELD_TRANSACTION_TYPE])
                .anyMatch(cell -> cell.startsWith("Foreign tax"));
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
        return distributionDetailRows().stream()
                .filter(row -> noteFormula.equals(row[Constants.DD_FIELD_NOTES]))
                .map(row -> row[Constants.DD_FIELD_SECURITY_DESCRIPTION])
                .findFirst()
                .orElse("");
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

    void transitionToDistributionDetailDataState() {
        setState(distributionDetailDataState());
    }

    void transitionToDistributionDetailHeaderState() {
        setState(distributionDetailHeaderState());
    }

    void transitionToForm1099DivState() {
        setState(form1099DivState());
    }

    void transitionToSearchState() {
        setState(searchState());
    }

    void transitionToSupplementalInfoState() {
        setState(supplementalInfoState());
    }

    private State distributionDetailDataState() {
        return this.distributionDetailDataState;
    }

    private State distributionDetailHeaderState() {
        return this.distributionDetailHeaderState;
    }

    private State form1099DivState() {
        return this.form1099DivState;
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
        return distributionDetailRows().stream()
                .map(row -> row[field])
                .anyMatch(cell -> cell.equals(value));
    }

    private List<String[]> distributionDetailRows() {
        return this.distributionDetailRows;
    }

    private List<String[]> form1099DivRows() {
        return this.form1099DivRows;
    }

    private State searchState() {
        return this.searchState;
    }

    private void setState(final State newState) {
        this.state = newState;
    }

    private List<String[]> supplementalInfoRows() {
        return this.supplementalInfoRows;
    }

    private State supplementalInfoState() {
        return this.supplementalInfoState;
    }
}
