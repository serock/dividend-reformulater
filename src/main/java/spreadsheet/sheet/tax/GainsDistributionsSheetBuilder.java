// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.List;

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

    private static final TableFilterField[] filterFields = createFilterFields();

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
        pivotTableHelper().setFilterFields(filterFields);
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
        final String[] types = new String[] {
                "Long-term capital gain",
                "Unrecaptured section 1250 gain"
                };
        final List<TableFilterField> fields = new ArrayList<>(types.length);

        TableFilterField field;

        for (String type : types) {
            field = new TableFilterField();
            field.Connection = FilterConnection.OR;
            field.Field = Constants.DD_FIELD_TRANSACTION_TYPE;
            field.IsNumeric = false;
            field.StringValue = type;
            field.Operator = FilterOperator.EQUAL;
            fields.add(field);
        }
        return fields.toArray(new TableFilterField[0]);
    }
}
