package com.rjn.thegamescompany.Global;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;


    public RealmController(Application application) {
        this.realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm istance
    public void refresh() {
        realm.refresh();
    }

    //clear all objects from Book.class
    public void clearAll() {
        realm.beginTransaction();
        realm.delete(Model_Fav_Games.class);
        realm.commitTransaction();
    }

    public RealmResults<Model_Fav_Games> getFavoriteGames() {
        return realm.where(Model_Fav_Games.class).findAll();
    }

    //query a single item with the given id
    public Model_Fav_Games getModelFavGames(String id) {
        return realm.where(Model_Fav_Games.class).equalTo("id", id).findFirst();
    }

    //query example
    public RealmResults<Model_Fav_Games> queryedBooks() {

        return realm.where(Model_Fav_Games.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }

    public boolean addFavGame(Element arrItem) {
        try {
            /*realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Model_Fav_Games model_fav_games = new Model_Fav_Games();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/
            Model_Fav_Games object = realm.where(Model_Fav_Games.class)
                    .equalTo("id", arrItem.getId())
                    .or()
                    .equalTo("name", arrItem.getName())
                    .findFirst();

            if (object == null) {
                Model_Fav_Games model_fav_games = new Model_Fav_Games();
                model_fav_games.setId(arrItem.getId());
                model_fav_games.setName(arrItem.getName());
                model_fav_games.setIcon(arrItem.getImage());
                model_fav_games.setHtml_flash(arrItem.getHtml_flash());
                model_fav_games.setPname(arrItem.getPname());
                realm.beginTransaction();
                realm.copyToRealm(model_fav_games);
                realm.commitTransaction();

                // Toast.makeText(this, "Added to favorite.", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Already added in favorite list.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFavGame(Element arrItem) {
        try {
            Model_Fav_Games employee = realm.where(Model_Fav_Games.class).equalTo("id", arrItem.getId()).findFirst();
            realm.where(Model_Fav_Games.class);
            if (employee != null) {
                if (!realm.isInTransaction()) {
                    realm.beginTransaction();
                }
                employee.deleteFromRealm();
                realm.commitTransaction();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean addRecentGameGame(Element arrItem) {
        try {
            /*realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        Model_Fav_Games model_fav_games = new Model_Fav_Games();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/
            Model_RecentPlay_Games object = realm.where(Model_RecentPlay_Games.class)
                    .equalTo("id", arrItem.getId())
                    .or()
                    .equalTo("name", arrItem.getName())
                    .findFirst();

            if (object == null) {
                Model_RecentPlay_Games model_fav_games = new Model_RecentPlay_Games();
                model_fav_games.setId(arrItem.getId());
                model_fav_games.setName(arrItem.getName());
                model_fav_games.setIcon(arrItem.getImage());
                model_fav_games.setHtml_flash(arrItem.getHtml_flash());
                model_fav_games.setPname(arrItem.getPname());
                realm.beginTransaction();
                realm.copyToRealm(model_fav_games);
                realm.commitTransaction();

                // Toast.makeText(this, "Added to favorite.", Toast.LENGTH_SHORT).show();
            } else {
                Model_RecentPlay_Games employee = realm.where(Model_RecentPlay_Games.class).equalTo("id", arrItem.getId()).findFirst();
                realm.where(Model_RecentPlay_Games.class);
                if (employee != null) {
                    if (!realm.isInTransaction()) {
                        realm.beginTransaction();
                    }
                    employee.deleteFromRealm();
                    realm.commitTransaction();
                }

                Model_RecentPlay_Games model_fav_games = new Model_RecentPlay_Games();
                model_fav_games.setId(arrItem.getId());
                model_fav_games.setName(arrItem.getName());
                model_fav_games.setIcon(arrItem.getImage());
                model_fav_games.setHtml_flash(arrItem.getHtml_flash());
                model_fav_games.setPname(arrItem.getPname());
                realm.beginTransaction();
                realm.copyToRealm(model_fav_games);
                realm.commitTransaction();

                // Toast.makeText(this, "Already added in favorite list.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RealmResults<Model_RecentPlay_Games> getecentPlayGames() {
        //RealmResults<Model_RecentPlay_Games> allRecentPlayed = realm.where(Model_RecentPlay_Games.class).findAll().sort("indexId", Sort.DESCENDING);
        RealmResults<Model_RecentPlay_Games> allRecentPlayed = realm.where(Model_RecentPlay_Games.class).findAll();
        return allRecentPlayed;
    }


}





