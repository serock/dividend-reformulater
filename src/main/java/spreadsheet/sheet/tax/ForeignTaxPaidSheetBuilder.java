// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.Collections;
import java.util.Set;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterConnection;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;
import text.Constants;

public class ForeignTaxPaidSheetBuilder extends PivotTableSheetBuilder {

    private Set<String> transactionTypes = Collections.emptySet();

    public ForeignTaxPaidSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {

        final XSpreadsheet foreignTaxPaidSheet = SpreadsheetDocumentHelper.addSheet(document(), "foreign-tax-paid");
        final CellAddress cellAddress = SheetHelper.getCellAddress(foreignTaxPaidSheet, 0, 0);

        pivotTableHelper().setTablesSupplier(foreignTaxPaidSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(createFilterFields());
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("foreign-tax-paid", cellAddress);

        sheetHelper().updateSheet(foreignTaxPaidSheet);
    }

    public void setTransactionTypes(final Set<String> types) {
        this.transactionTypes = types;
    }

    private TableFilterField[] createFilterFields() {
        final int size = transactionTypes().size();
        final String[] types = transactionTypes().toArray(new String[size]);
        final TableFilterField[] filterFields = new TableFilterField[size];
        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = types[0];
        filterFields[0].Operator = FilterOperator.EQUAL;
        for (int i = 1; i < size; i++) {
            filterFields[i] = new TableFilterField();
            filterFields[i].Connection = FilterConnection.OR;
            filterFields[i].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
            filterFields[i].IsNumeric = false;
            filterFields[i].StringValue = types[i];
            filterFields[i].Operator = FilterOperator.EQUAL;
        }
        return filterFields;
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private Set<String> transactionTypes() {
        return this.transactionTypes;
    }
}
