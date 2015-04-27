package dev.jinkim.snappollandroid.ui.invitefriends;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;

/**
 * Created by Jin on 4/3/15.
 *
 * Dialog for choosing friends to invite to a poll - show list of Google+ friends and invitation status
 */
public class InviteFriendsDialog {
    public static String TAG = InviteFriendsDialog.class.getSimpleName();
    private Context mContext;
    private InviteFriendsController controller;

    private View dialogContent;

    private ListView listView;
    //    private SelectedFriendListAdapter adapter;
    private ChooseFriendListAdapter adapter;
    private SearchView svSearch;
    private Button btnSelectAll;
    private Button btnUnselectAll;

    private List<RowFriend> gPlusFriends;
    private List<RowFriend> selectedFriends;
    private SparseBooleanArray selected;

    private boolean updated = false;

    public InviteFriendsDialog(Context context, InviteFriendsController controller, List<RowFriend> retrievedFriends) {
        mContext = context;
        gPlusFriends = retrievedFriends;
        this.controller = controller;
        setupDialog();
    }

    public void setupDialog() {
        // set up custom view for dialog - color indicator, edit text
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogContent = vi.inflate(R.layout.dialog_content_choose_friends, null);

        listView = (ListView) dialogContent.findViewById(R.id.friends_dialog_lv_choose_friends);
        svSearch = (SearchView) dialogContent.findViewById(R.id.friends_dialog_sv_search);
        btnSelectAll = (Button) dialogContent.findViewById(R.id.btn_dialog_select_all);
        btnUnselectAll = (Button) dialogContent.findViewById(R.id.btn_dialog_unselect_all);

        adapter = new ChooseFriendListAdapter(mContext, gPlusFriends);

        // sparse boolean array to keep up with selected friend list for display
        selected = new SparseBooleanArray(gPlusFriends.size());

        /* SET UP SELECT FRIENDS LIST VIEW */
        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RowFriend item = adapter.getItem(position);
                updated = true;

                if (item.selected) {
                    item.selected = false;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = false;
                    selected.delete(position);
                } else {
                    item.selected = true;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = true;
                    selected.append(position, true);
                }
                Log.d(TAG, "Clicked friend pos(" + String.valueOf(position) + "), id(" + String.valueOf(id) + ")");
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the flag to enable web call
                updated = true;
                int index = 0;
                for (RowFriend r : adapter.getAllFriends()) {
                    r.selected = true;
                    selected.append(index, true);
                    index++;
                }
                adapter.notifyDataSetChanged();
            }
        });

        btnUnselectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the flag to enable web call
                updated = true;
                for (RowFriend r : adapter.getAllFriends()) {
                    r.selected = false;
                    selected.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * display list of all G+ friends so that user can choose friends to invite to the poll
     */
    public void showDialog() {

        updated = false;

        for (int i = 0; i < gPlusFriends.size(); i++) {
            // if preselected from existing list, then update the sparse array
            if (gPlusFriends.get(i).selected) {
                selected.append(i, true);
            }
        }

        svSearch.setQueryHint(mContext.getString(R.string.query_hint_search_from_gplus));
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });

        /* SET UP SELECT FRIENDS DIALOG */
        String dialogTitle = mContext.getResources().getString(R.string.dialog_title_choose_frineds_gplus);

        boolean wrapInScrollView = false;
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(dialogTitle)
                .customView(dialogContent, wrapInScrollView)
                .positiveText(R.string.action_save)
//                .positiveText(R.string.agree)
                .negativeText(R.string.action_cancel)
//                .negativeText(R.string.disagree)
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        /* update selected friends */
                        Log.d(TAG, "selected: " + selected.toString());

                        // if there is any change (item click toggle) then proceed
                        if (updated) {
                            Log.d(TAG, "Update list");
                            // TODO: grab the keys (positions) that are true in value
                            selectedFriends = new ArrayList<RowFriend>();
                            for (int i = 0; i < selected.size(); i++) {
                                int position = selected.keyAt(i);
                                if (selected.valueAt(i)) {
                                    // grab the selected friends from the original list (inst of filteredList)
                                    RowFriend item = adapter.getItemFromOriginalList(adapter.getOriginalIndex(position));
                                    selectedFriends.add(item);
                                }
                            }

                            Log.d(TAG, "# Selected friends: " + String.valueOf(selectedFriends.size()));
                            // add to poll_invites table
                            controller.inviteSelectedFriends(selectedFriends);

                            // update inviteeIds in controller
                            List<String> invitees = new ArrayList<>();
                            for (RowFriend r : selectedFriends) {
                                invitees.add(r.person.getId());
                            }
                            controller.setPollInviteeIds(invitees);
                        }

                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        showingDialog = false;
                    }
                })
                .show();
//        dialog.show();
    }

    /**
     * After fetching existing invitees from poll invitees, reflect them on the list as already-invited
     *
     * @param inviteeIds List of G+ ids already invited to this poll
     */
    public void updateExistingInvitees(List<String> inviteeIds) {
        if (selectedFriends == null) {
            return;
        }

        for (String id : inviteeIds) {
            for (RowFriend r : gPlusFriends) {
                if (r.person.getId().equals(id)) {
                    r.selected = true;
                }
            }
            // TODO: reflect selected UI
        }

        adapter.notifyDataSetChanged();
    }
}
