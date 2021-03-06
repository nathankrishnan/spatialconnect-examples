package com.boundlessgeo.spatialconnect.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.boundlessgeo.spatialconnect.services.SCServiceManager;
import com.boundlessgeo.spatialconnect.stores.SCDataStore;
import com.boundlessgeo.spatialconnect.stores.SCStoreStatusEvent;

import java.util.List;

import rx.functions.Action1;

/**
 * The DataStoreManagerFragment displays a list of active SCDataStore instances that the user can choose from to
 * see details about the selected store.
 */
public class DataStoreManagerFragment extends Fragment implements ListView.OnItemClickListener {

    private ListView listView;
    private FragmentManager fragmentManager;
    protected SCServiceManager serviceManager;
    private ArrayAdapter<SCDataStore> storeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate the views
        View view = inflater.inflate(R.layout.fragment_manager, null);
        listView = (ListView) view.findViewById(R.id.fragment_manager_list);
        fragmentManager = getFragmentManager();

        // Set the list's click listener
        listView.setOnItemClickListener(this);

        // Set the adapter for the list view
        List<SCDataStore> s = serviceManager.getDataService().getAllStores();
        storeAdapter = new ArrayAdapter<SCDataStore>(
                getActivity(),
                R.layout.drawer_list_item,
                s
        );
        listView.setAdapter(storeAdapter);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceManager = SpatialConnectService.getInstance().getServiceManager(getActivity());
        serviceManager.startAllServices();
        serviceManager.getDataService().storeEvents.subscribe(new Action1<SCStoreStatusEvent>() {
            @Override
            public void call(SCStoreStatusEvent scStoreStatusEvent) {
                storeAdapter.clear();
                storeAdapter.addAll(serviceManager.getDataService().getAllStores());
                storeAdapter.notifyDataSetChanged();
            }
        });
        serviceManager.getDataService().storeEvents.connect();
    }

    public void onDataStoreSelected(SCDataStore dataStore) {
        Intent intent = new Intent(getActivity(), DataStoreDetailsActivity.class);
        intent.putExtra("id", dataStore.getStoreId());
        startActivity(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (hidden) {
            //fragment became visible
            //your code here
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        listView.setItemChecked(position, true);
        SCDataStore s = (SCDataStore) listView.getItemAtPosition(position);
        this.onDataStoreSelected(s);
    }

}


