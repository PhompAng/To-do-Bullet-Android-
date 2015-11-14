package th.in.phompang.todobullet.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import th.in.phompang.todobullet.R;
import th.in.phompang.todobullet.Task;
import th.in.phompang.todobullet.TaskList;
import th.in.phompang.todobullet.activity.AddTaskActivity;
import th.in.phompang.todobullet.helper.TaskAdapter;
import th.in.phompang.todobullet.helper.SQLiteHandler;
import th.in.phompang.todobullet.helper.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements TaskAdapter.ViewHolder.ClickListener{

    private SQLiteHandler db;
    private SessionManager session;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Activity mActivity;

    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    private ArrayList<Task> dataset;

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        db = new SQLiteHandler(mActivity);
        session = new SessionManager(mActivity);

        if(!session.isLogin()) {
            logoutUser();
        }

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TaskAdapter(getActivity(), initTask(), this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton new_task_text = (FloatingActionButton) v.findViewById(R.id.new_task_text);
        new_task_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTaskDialog(0);
            }
        });

        FloatingActionButton new_task_list = (FloatingActionButton) v.findViewById(R.id.new_task_list);
        new_task_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTaskDialog(1);
            }
        });

        if (!getArguments().isEmpty()) {
            Bundle arg = getArguments();
            switch (arg.getInt("type", -1)) {
                case 0:
                    Log.d("arg", Integer.toString(arg.getInt("type")));
                    addItem(arg.getString("title"), arg.getString("description"), arg.getString("datetime"), 0);
                    break;
                case 1:
                    ArrayList<TaskList> lst = arg.getParcelableArrayList("list");
                    addItem(arg.getString("title"), lst, arg.getString("datetime"), 1);
                    break;
                default:
                    break;
            }

        }

        return v;
    }

    private void showNewTaskDialog(int type) {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        switch (type) {
            case 0:
                intent.putExtra("type", 0);
                break;
            case 1:
                intent.putExtra("type", 1);
                break;
        }
        startActivity(intent);
    }

    private void addItem(String title, String description, String datetime, int type) {
        long id = db.addTask(title, description, type, datetime);
        if (id != -1) {
            dataset.add(new Task(id, title, description, datetime, Task.TYPE_TEXT));
        }

        mAdapter.notifyDataSetChanged();
    }

    private void addItem(String title, ArrayList<TaskList> lst, String datetime, int type) {
        Gson gson = new Gson();
        String arrayList = gson.toJson(lst);
        Log.d("arrayList", arrayList);

        long id = db.addTask(title, arrayList, type, datetime);
        if (id != -1) {
            dataset.add(new Task(id, title, lst, datetime, Task.TYPE_LIST));
        }

        mAdapter.notifyDataSetChanged();
    }


    private ArrayList<Task> initTask() {
        dataset = new ArrayList<Task>();
        ArrayList<HashMap<String, String>> tasks = db.getTaskDeails();

        for (HashMap<String, String> task: tasks) {
            int type = Integer.parseInt(task.get("type"));
            switch (type) {
                case 0:
                    dataset.add(new Task(Long.parseLong(task.get("id")), task.get("title"), task.get("description"), task.get("time"), type));
                    break;
                case 1:
                    ArrayList<TaskList> lst = new ArrayList<>();

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonArray jArray = parser.parse(task.get("description")).getAsJsonArray();

                    for(JsonElement obj : jArray )
                    {
                        TaskList tl = gson.fromJson(obj , TaskList.class);
                        lst.add(tl);
                    }

                    for (TaskList t:
                            lst) {
                        Log.d("finalOutputString", t.getName());
                    }

                    dataset.add(new Task(Long.parseLong(task.get("id")), task.get("title"), lst, task.get("time"), type));
                    break;
            }

        }

        return dataset;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        db.deleteTask();
        pref = mActivity.getSharedPreferences("token", 0);
        editor = pref.edit();

        editor.clear();
        editor.commit();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new LoginFragment().newInstance()).commit();
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    removeItem();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            actionMode = null;
        }
    }

    public void removeItem() {
        List<Integer> task;
        task = mAdapter.getSelectedItems();
        Collections.sort(task);
        Collections.reverse(task);
        for (Integer i: task) {
            mAdapter.removeAt(i);
        }
    }
}
