package com.adrielcafe.stask.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.adrielcafe.stask.App;
import com.adrielcafe.stask.R;
import com.adrielcafe.stask.adapter.TaskAdapter;
import com.adrielcafe.stask.model.Task;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.melnykov.fab.FloatingActionButton;
import com.orm.query.Select;

import java.util.List;

public class TasksActivity extends ActionBarActivity {
    private List<Task> tasks;
    private ListView tasksList;
    private TaskAdapter tasksAdapter;
    private ImageView background;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo);
        setContentView(R.layout.activity_tasks);

        tasksList = (ListView) findViewById(android.R.id.list);
        background = (ImageView) findViewById(android.R.id.background);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(tasksList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTask();
            }
        });

        loadTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            loadTasks();
        }
    }

    private void loadTasks(){
        tasks = Select
                .from(Task.class)
                .orderBy("id DESC")
                .list();

        if(tasks.isEmpty()){
            background.setVisibility(View.VISIBLE);
            tasksList.setVisibility(View.GONE);
        } else {
            background.setVisibility(View.GONE);
            tasksList.setVisibility(View.VISIBLE);
            tasksAdapter = new TaskAdapter(this, tasks);
            tasksList.setAdapter(tasksAdapter);
        }
    }

    public void newTask(){
        Intent i = new Intent(this, TaskEditActivity.class);
        startActivityForResult(i, 0);
    }

    public void editTask(Task task){
        Intent i = new Intent(this, TaskEditActivity.class);
        i.putExtra(App.EXTRA_TASK_ID, task.getId());
        startActivityForResult(i, 0);
    }

    public void deleteTask(Task task){
        task.delete();
        tasks.remove(task);
        tasksAdapter.notifyDataSetChanged();
    }

    public void showTask(final Task task){
        final NiftyDialogBuilder dialog = NiftyDialogBuilder.getInstance(this);
        dialog.withTitle(task.title)
                .withMessage(task.description)
                .withDialogColor(getResources().getColor(R.color.primary))
                .withEffect(Effectstype.SlideBottom)
                .withDuration(300)
                .withButton1Text(getString(R.string.close))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                })
                .show();
    }

    public void toggleTaskStatus(Task task, View statusView){
        if(task.done){
            task.done = false;
            statusView.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            task.done = true;
            statusView.setBackgroundColor(getResources().getColor(R.color.accent));
        }
        task.save();
    }
}