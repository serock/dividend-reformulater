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

public class GainsDistributionsSheetBuilder extends PivotTableSheetBuilder {

    private static final TableFilterField[] tableFilterFields = createFilterFields();

    public GainsDistributionsSheetBuilder() {
        super();
    }

    @Override
    public void build() throws com.sun.star.uno.Exception {
        final XSpreadsheet gainsDistributionsSheet = SpreadsheetDocumentHelper.addSheet(document(), "gains-distributions");
        final CellAddress cellAddress = SheetHelper.getCellAddress(gainsDistributionsSheet, 0, 0);

        pivotTableHelper().initialize(gainsDistributionsSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(tableFilterFields);
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("capital-gain-distributions", cellAddress);

        sheetHelper().updateSheet(gainsDistributionsSheet, true);
        SpreadsheetDocumentHelper.setActiveSheet(document(), gainsDistributionsSheet);
        SpreadsheetDocumentHelper.freezeRowsOfActiveSheet(document(), 2);
    }

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private static TableFilterField[] createFilterFields() {
        final TableFilterField[] filterFields = new TableFilterField[2];

        filterFields[0] = new TableFilterField();
        filterFields[0].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[0].IsNumeric = false;
        filterFields[0].StringValue = "Long-term capital gain";
        filterFields[0].Operator = FilterOperator.EQUAL;

        filterFields[1] = new TableFilterField();
        filterFields[1].Connection = FilterConnection.OR;
        filterFields[1].Field = Constants.DD_FIELD_TRANSACTION_TYPE;
        filterFields[1].IsNumeric = false;
        filterFields[1].StringValue = "Unrecaptured section 1250 gain";
        filterFields[1].Operator = FilterOperator.EQUAL;

        return filterFields;
    }
}
