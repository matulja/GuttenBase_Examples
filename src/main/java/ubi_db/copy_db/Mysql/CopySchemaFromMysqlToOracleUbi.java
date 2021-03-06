package ubi_db.copy_db.Mysql;


import de.akquinet.jbosscc.guttenbase.hints.ColumnTypeMapperHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.mapping.DefaultColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.repository.impl.ConnectorRepositoryImpl;
import de.akquinet.jbosscc.guttenbase.tools.DefaultTableCopyTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.CopySchemaTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.comparison.SchemaComparatorTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.comparison.SchemaCompatibilityIssues;
import ubi_db.connInfo.MysqlConnetionsInfoUbi;
import ubi_db.connInfo.OracleConnetionsInfoUbi;

import java.sql.SQLException;


/**
 * Created by mfehler on 26.05.17.
 */
public class CopySchemaFromMysqlToOracleUbi {


    public static final String SOURCE = "source";
    public static final String TARGET = "target";


    public static void main(final String[] args) throws Exception {

        final ConnectorRepository connectorRepository = new ConnectorRepositoryImpl();

        connectorRepository.addConnectionInfo(SOURCE, new MysqlConnetionsInfoUbi());
        connectorRepository.addConnectionInfo(TARGET, new OracleConnetionsInfoUbi());


        connectorRepository.addConnectorHint(SOURCE, new ColumnTypeMapperHint() {
                    @Override
                    public ColumnTypeMapper getValue() {
                         return new DefaultColumnTypeMapper();
                    }
                });



        new CopySchemaTool(connectorRepository).copySchema(SOURCE, TARGET);
        System.out.println("------------------------------------ SCHEMA DONE ------------------------------------ ");

        SchemaCompatibilityIssues issues = new SchemaComparatorTool(connectorRepository).check(SOURCE, TARGET);
        System.out.println("------------------------------------ COPY DATA ------------------------------------ ");

          try {

            new DefaultTableCopyTool(connectorRepository).copyTables(SOURCE, TARGET);


          } catch (SQLException se) {

            if (!issues.isSevere()) {

              int count = 1;
              while (se != null) {
                System.out.println("SQLException " + count);
                System.out.println("Code: " + se.getErrorCode());
                System.out.println("SqlState: " + se.getSQLState());
                System.out.println("Error Message: " + se.getMessage());
                se = se.getNextException();
                count++;

              }

            }

      }

      System.out.println("CopyData Done !!!");
    }
}







