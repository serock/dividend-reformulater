// SPDX-License-Identifier: MIT
package spreadsheet.sheet.tax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        pivotTableHelper().initialize(foreignTaxPaidSheet);
        pivotTableHelper().setSourceRange(getSourceRange());
        pivotTableHelper().setRowOrientation(Constants.DD_FIELD_SECURITY_DESCRIPTION);
        pivotTableHelper().setColumnOrientation(Constants.DD_FIELD_TRANSACTION_TYPE);
        pivotTableHelper().setDataOrientation(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setSumFunction(Constants.DD_FIELD_AMOUNT);
        pivotTableHelper().setFilterFields(createFilterFields());
        pivotTableHelper().showFilterButton(false);
        pivotTableHelper().insertPivotTable("foreign-tax-paid", cellAddress);

        sheetHelper().updateSheet(foreignTaxPaidSheet, true);
    }

    public void setTransactionTypes(final Set<String> types) {
        this.transactionTypes = types;
    }

    private TableFilterField[] createFilterFields() {
        final String[] types = transactionTypes().toArray(new String[0]);
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

    private CellRangeAddress getSourceRange() throws WrappedTargetException, NoSuchElementException {
        final XSpreadsheet dividendDetailSheet = SpreadsheetDocumentHelper.getSheet(document(), "dividend-detail");
        return SheetHelper.getCellRangeAddressOfUsedArea(dividendDetailSheet);
    }

    private Set<String> transactionTypes() {
        return this.transactionTypes;
    }
}
