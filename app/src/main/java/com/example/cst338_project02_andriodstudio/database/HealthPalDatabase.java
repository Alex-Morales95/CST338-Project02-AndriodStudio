package com.example.cst338_project02_andriodstudio.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cst338_project02_andriodstudio.database.entities.HealthPal;
import com.example.cst338_project02_andriodstudio.MainActivity;
import com.example.cst338_project02_andriodstudio.database.entities.User;
import com.example.cst338_project02_andriodstudio.database.typeConverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters(LocalDateTypeConverter.class)
@Database(entities = {HealthPal.class, User.class}, version = 1, exportSchema = false)
public abstract class HealthPalDatabase extends RoomDatabase {

    public static final String USER_TABLE = "usertable";
    private static final String DATABASE_NAME = "HealthPalDatabase";
    public static final String HEALTH_PAL_TABLE = "healthPalTable";

    private static volatile HealthPalDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HealthPalDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (HealthPalDatabase.class){
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    HealthPalDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
            Log.i(MainActivity.TAG, "DATABASE CREATED");
            databaseWriteExecutor.execute(()-> {
                UserDAO dao = INSTANCE.userDAO();
                dao.deleteAll();
                User admin = new User("admin1","admin1");
                admin.setAdmin(true);
                dao.insert(admin);
                User testUser1 = new User("testuser1","testuser1");
                dao.insert(testUser1);
            });
        }
    };

    public abstract HealthPalDAO healthPalDAO();

    public abstract UserDAO userDAO();
}