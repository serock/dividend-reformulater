// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.FilterOperator;
import com.sun.star.sheet.TableFilterField;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.CellAddress;
import com.sun.star.table.CellRangeAddress;

import spreadsheet.SpreadsheetDocumentHelper;
import spreadsheet.sheet.PivotTableSheetBuilder;
import spreadsheet.sheet.SheetHelper;

public class NondividendDistributionsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public NondividendDistributionsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet nondividendDistributionsSheet = SpreadsheetDocumentHelper.addSheet(document(), "nondividend-distributions");
        final CellAddress cellAddress = SheetHelper.getCellAddress(nondividendDistributionsSheet, 0, 0);

        pivotTableHelper().setTablesSupplier(nondividendDistributionsSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(DividendDetailSheetBuilder.FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(DividendDetailSheetBuilder.FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("nondividend-distributions", cellAddress);

        sheetHelper().updateSheet(nondividendDistributionsSheet);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[1];
        setNondividendFilter(filterFields);
        return filterFields;
    }

    private static void setNondividendFilter(final TableFilterField[] filterFields) {
        filterFields[0] = new TableFilterField();
        filterFields[0].Field = DividendDetailSheetBuilder.FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Nondividend distribution";
        filterFields[0].Operator = FilterOperator.EQUAL;
    }
}
