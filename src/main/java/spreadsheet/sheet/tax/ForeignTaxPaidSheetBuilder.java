// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

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

public class ForeignTaxPaidSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public ForeignTaxPaidSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {

        final XSpreadsheet foreignTaxPaidSheet = SpreadsheetDocumentHelper.addSheet(document(), "foreign-tax-paid");
        final CellAddress cellAddress = SheetHelper.getCellAddress(foreignTaxPaidSheet, 0, 0);

        pivotTableHelper().setTablesSupplier(foreignTaxPaidSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(DividendDetailSheetBuilder.FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("foreign-tax-paid", cellAddress);

        sheetHelper().updateSheet(foreignTaxPaidSheet);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[2];
        setFirstForeignTaxAdjustmentFilter(filterFields);
        setLastForeignTaxWithheldFilter(filterFields);
        return filterFields;
    }

    private static void setFirstForeignTaxAdjustmentFilter(final TableFilterField[] filterFields) {
        filterFields[0] = new TableFilterField();
        filterFields[0].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Adj- Foreign tax withheld-AA";
        filterFields[0].Operator = FilterOperator.GREATER;
    }

    private static void setLastForeignTaxWithheldFilter(TableFilterField[] filterFields) {
        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.AND;
        filterFields[1].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Foreign tax withheld-ZZ";
        filterFields[1].Operator = FilterOperator.LESS;
    }
}
