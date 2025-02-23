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
import text.Constants;

public class TaxExemptDividendsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public TaxExemptDividendsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {

        final XSpreadsheet taxExemptDividendsSheet = SpreadsheetDocumentHelper.addSheet(document(), "tax-exempt");
        final CellAddress cellAddress = SheetHelper.getCellAddress(taxExemptDividendsSheet, 0, 0);

        pivotTableHelper().initialize(taxExemptDividendsSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("tax-exempt-dividends", cellAddress);

        sheetHelper().updateSheet(taxExemptDividendsSheet);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[2];
        setTaxExemptDividendFilter(filterFields);
        setTaxExemptDividendAMTFilter(filterFields);
        return filterFields;
    }

    private static void setTaxExemptDividendFilter(final TableFilterField[] filterFields) {
        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Tax-exempt dividend";
        filterFields[0].Operator = FilterOperator.EQUAL;
    }

    private static void setTaxExemptDividendAMTFilter(final TableFilterField[] filterFields) {
        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.OR;
        filterFields[1].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Tax-exempt dividend AMT";
        filterFields[1].Operator = FilterOperator.EQUAL;
    }
}
