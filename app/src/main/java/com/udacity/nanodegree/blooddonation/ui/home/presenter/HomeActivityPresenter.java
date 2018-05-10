package com.udacity.nanodegree.blooddonation.ui.home.presenter;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.udacity.nanodegree.blooddonation.R;
import com.udacity.nanodegree.blooddonation.constants.FireBaseConstants;
import com.udacity.nanodegree.blooddonation.data.model.ReceiverDonorRequestType;
import com.udacity.nanodegree.blooddonation.data.model.User;
import com.udacity.nanodegree.blooddonation.ui.home.HomeActivityContract;
import com.udacity.nanodegree.blooddonation.ui.home.model.RequestDetails;

/**
 * Created by Ankush Grover(ankushgrover02@gmail.com) on 23/04/2018.
 */
public class HomeActivityPresenter
        implements HomeActivityContract.Presenter {

    private final HomeActivityContract.View mView;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseDatabase mFireBaseDataBase;

    private ValueEventListener mUserDetailsValueEventListener;
    private ValueEventListener mUserRequestDetailsValueEventListener;
    private ValueEventListener mDonorDetailsValueEventListener;

    private DatabaseReference mUserDetailDabaseReference;
    private DatabaseReference mUserRequestDetailsDatabaseRef;
    private DatabaseReference mDonorDetailsDatabaseRef;


    private User mUser;
    private ReceiverDonorRequestType mReceiverDonorRequestType;

    //private GeoFire geoFire;
    //private GeoQuery geoQuery;

    public HomeActivityPresenter(HomeActivityContract.View view, FirebaseAuth firebaseAuth,
                                 FirebaseDatabase firebaseDatabase) {
        this.mView = view;
        mFireBaseAuth = firebaseAuth;
        mFireBaseDataBase = firebaseDatabase;
    }

    @Override
    public void onCurrentLocationClicked() {

        mView.fetchCurrentLocation();
    }

    @Override
    public void onAddClicked() {
        if (mUser == null)
            mView.generalResponse(R.string.msg_please_wait);
        else
            mView.openCreateRequestDialog(mUser);
    }

    @Override
    public void onCreate() {

        createUserDetailsValueEventListener();
        createUserRequestDetailsValueEventListener();
        createDonorDetailsValueEventListener();

        mUserDetailDabaseReference = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.USERS)
                .child(mFireBaseAuth.getUid());

        mUserRequestDetailsDatabaseRef = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.RECEIVER);


        mDonorDetailsDatabaseRef = mFireBaseDataBase.getReference()
                .child(FireBaseConstants.DONOR);


        mUserDetailDabaseReference.addListenerForSingleValueEvent(mUserDetailsValueEventListener);
    }

    private void createDonorDetailsValueEventListener() {
        mDonorDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //todo Add logic

                mView.showHideLoader(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mView.showHideLoader(false);

            }
        };
    }

    private void createUserDetailsValueEventListener() {
        mUserDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mUser = dataSnapshot.getValue(User.class);
                }
                mUserRequestDetailsDatabaseRef.addListenerForSingleValueEvent(
                        mUserRequestDetailsValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void createUserRequestDetailsValueEventListener() {
        mUserRequestDetailsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //todo modify for list of data.
                if (dataSnapshot.exists()) {
                    mReceiverDonorRequestType = dataSnapshot.getValue(ReceiverDonorRequestType.class);
                }
                updateCameraAfterGettingUserRequestDetails();

                mDonorDetailsDatabaseRef.addListenerForSingleValueEvent(mDonorDetailsValueEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateCameraAfterGettingUserRequestDetails();
            }
        };
    }

  /*private void querytAtLocation(GeoLocation geoLocation, int radius) {
    // Query at 1 km
    geoQuery = geoFire.queryAtLocation(geoLocation, radius);
  }*/

  /*@Override public void queryGeoFire(LatLng latLng) {
    geoFire = new GeoFire(
        mFireBaseDataBase.getReference().child(FireBaseConstants.DONOR).child(mUser.bloodGroup));

    querytAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 1);
  }*/

  /*@Override public void queryGeoFire(LatLng latLng, String bgp) {
    geoFire =
        new GeoFire(mFireBaseDataBase.getReference().child(FireBaseConstants.DONOR).child(bgp));

    // Query at 1 km
    querytAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 1);
  }*/

    synchronized private void updateCameraAfterGettingUserRequestDetails() {
        LatLng latLng;
        if (mReceiverDonorRequestType != null) {
            latLng = new LatLng(mReceiverDonorRequestType.getLocation().getLatitude(),
                    mReceiverDonorRequestType.getLocation().getLongitude());
            mView.addRequestMarker(mReceiverDonorRequestType);
        } else {
            latLng = new LatLng(mUser.latitude, mUser.longitude);
        }
        mView.updateCamera(latLng);

        //queryGeoFire(latLng);
        //geoQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onStart() {
    /*if (geoQuery != null) {
      geoQuery.removeAllListeners();
      geoQuery.addGeoQueryEventListener(this);
    }*/
    }

    @Override
    public void onStop() {
    /*if (geoQuery != null) {
      geoQuery.removeAllListeners();
    }*/
    }

    @Override
    public void onDestroy() {
        mUserDetailDabaseReference.removeEventListener(mUserDetailsValueEventListener);
        mUserRequestDetailsDatabaseRef.removeEventListener(mUserRequestDetailsValueEventListener);
    }

    @Override
    public void onBloodRequest(RequestDetails requestDetails) {

    }

    @Override
    public void onDonateRequest(RequestDetails requestDetails) {

    }

    // GeoQuery Listeners
  /*@Override public void onKeyEntered(String key, GeoLocation location) {
    mView.putGeoKeyMarker(key, location);
  }

  @Override public void onKeyExited(String key) {
    mView.removeOldGeoMarker(key);
  }

  @Override public void onKeyMoved(String key, GeoLocation location) {
    mView.animateGeoMarker(key, location);
  }

  @Override public void onGeoQueryReady() {
  }

  @Override public void onGeoQueryError(DatabaseError error) {
    mView.showGeoQueryErrorDialogBox(error);
  }*/
}
