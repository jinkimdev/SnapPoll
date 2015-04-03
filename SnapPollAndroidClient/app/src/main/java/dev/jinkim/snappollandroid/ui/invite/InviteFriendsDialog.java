package dev.jinkim.snappollandroid.ui.invite;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import dev.jinkim.snappollandroid.R;
import dev.jinkim.snappollandroid.model.RowFriend;
import dev.jinkim.snappollandroid.ui.newpoll.ChooseFriendListAdapter;

/**
 * Created by Jin on 4/3/15.
 */
public class InviteFriendsDialog {
    public static String TAG = InviteFriendsDialog.class.getSimpleName();
    private Context mContext;

    private View dialogContent;

    private ListView listView;
    //    private SelectedFriendListAdapter adapter;
    private ChooseFriendListAdapter adapter;
    private SearchView svSearch;

    private List<RowFriend> gPlusFriends;
    private List<RowFriend> selectedFriends;
    private SparseBooleanArray selected;


    /**
     * After fetching existing invitees from poll invitees, reflect them on the list as already-invited
     *
     * @param inviteeIds    List of G+ ids already invited to this poll
     */
    public void updateExistingInvitees(List<String> inviteeIds) {
        if (selectedFriends == null) {
            return;
        }

        for (String idr : inviteeIds) {
            // TODO: reflect selected UI
            // TODO: add to selected array
        }
    }

    public interface InviteFriendsCallback {
        void onFriendsSelected(List<RowFriend> friendsSelectedFromDialog);
    }

    public InviteFriendsDialog(Context context, List<RowFriend> retrievedFriends) {
        mContext = context;
        gPlusFriends = retrievedFriends;
        setupDialog();
    }

    public void setupDialog() {
        // set up custom view for dialog - color indicator, edit text
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogContent = vi.inflate(R.layout.dialog_content_choose_friends, null);

        listView = (ListView) dialogContent.findViewById(R.id.friends_dialog_lv_choose_friends);
        svSearch = (SearchView) dialogContent.findViewById(R.id.friends_dialog_sv_search);

        ChooseFriendListAdapter adapter = new ChooseFriendListAdapter(mContext, gPlusFriends);

        // sparse boolean array to keep up with selected friend list
        selected = new SparseBooleanArray(gPlusFriends.size());
    }

    /**
     * display list of all G+ friends so that user can choose friends to invite to the poll
     */
    public void showDialog(final InviteFriendsCallback callback) {


        for (int i = 0; i < gPlusFriends.size(); i++) {
            // if preselected from existing list, then update the sparse array
            if (gPlusFriends.get(i).selected) {
                selected.append(i, true);
            }
        }

        /* SET UP SELECT FRIENDS LIST VIEW */

        listView.setTextFilterEnabled(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).selected) {
                    adapter.getItem(position).selected = false;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = false;
                    selected.delete(position);
                } else {
                    adapter.getItem(position).selected = true;
                    adapter.getItemFromOriginalList(adapter.getOriginalIndex(position)).selected = true;
                    selected.append(position, true);
                }
                Log.d(TAG, "Selected friend position: " + String.valueOf(position));
                Log.d(TAG, "Selected friend id: " + String.valueOf(id));
                adapter.notifyDataSetChanged();
            }
        });
        listView.setAdapter(adapter);


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
                        Log.d(TAG, selected.toString());

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


                        callback.onFriendsSelected(selectedFriends);
//                        updateSelectedFriends(selectedFriends);

                        Log.d(TAG, "Selected friends: " + String.valueOf(selectedFriends.size()));
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        showingDialog = false;
                    }
                })
                .show();
        dialog.show();
    }
}
