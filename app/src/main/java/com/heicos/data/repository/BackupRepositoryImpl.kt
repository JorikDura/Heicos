package com.heicos.data.repository

import android.database.Cursor
import android.os.Environment.getExternalStorageDirectory
import androidx.sqlite.db.SimpleSQLiteQuery
import com.heicos.data.database.CosplaysDataBase
import com.heicos.domain.repository.BackupRepository
import com.heicos.utils.csv.CSVReader
import com.heicos.utils.csv.CSVWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val database: CosplaysDataBase
) : BackupRepository {
    override fun import(tableName: String) {
        val csvReader =
            CSVReader(FileReader("${getExternalStorageDirectory()}/heicos_backup/$tableName.csv"))
        var nextLine: Array<String>? = null
        var count = 0
        val columns = StringBuilder()
        do {
            val value = StringBuilder()
            nextLine = csvReader.readNext()
            nextLine?.let { line ->
                for (i in line.indices) {

                    if (line[i].contains("'")) {
                        line[i] = line[i].replace("'", "''")
                    }

                    if (count == 0) {
                        if (i == line.size - 1) {
                            columns.append(line[i])
                            count = 1
                        } else
                            columns.append(line[i]).append(",")
                    } else {
                        if (i == line.size - 1) {
                            value.append("'").append(line[i]).append("'")
                            count = 2
                        } else {
                            if (line[i].isBlank())
                                value.append("null,")
                            else
                                value.append("'").append(line[i]).append("',")
                        }

                    }
                }
                if (count == 2) {
                    insert(tableName, columns, value)
                }
            }
        } while ((nextLine) != null)
    }

    override fun export(tableName: String) {
        val exportDir = File(getExternalStorageDirectory(), "/heicos_backup")

        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        val file = File(exportDir, "$tableName.csv")

        file.createNewFile()
        val csvWrite = CSVWriter(FileWriter(file))
        val curCSV: Cursor = database.query("SELECT * FROM $tableName", null)

        val columns = curCSV.columnNames.toMutableList()
        val id = columns.find { column ->
            column == "id"
        }
        if (!id.isNullOrBlank())
            columns.remove(id)

        csvWrite.writeNext(columns.toTypedArray())
        while (curCSV.moveToNext()) {
            val arrStr = arrayOfNulls<String>(curCSV.columnCount - 1)
            for (i in 1 until curCSV.columnCount) arrStr[i - 1] = curCSV.getString(i)
            csvWrite.writeNext(arrStr)
        }
        csvWrite.close()
        curCSV.close()
    }

    private fun insert(tableName: String, columns: StringBuilder, values: StringBuilder) {
        val query = SimpleSQLiteQuery(
            "INSERT INTO $tableName ($columns) values($values)",
            arrayOf()
        )

        database.backupDao.insertDataRawFormat(query)
    }
}