package com.example.campusquest;


import android.os.Parcel;
import android.os.Parcelable;

public final class QuestInfo implements Parcelable {

    private final String mQuestId;
    private final String mQuestName;
    private final int mTotalStages;

    public QuestInfo(String questId, String questName, int totalStages) {
        mQuestId= questId;
        mQuestName = questName;
        mTotalStages = totalStages;
    }

    private QuestInfo(Parcel source) {
        mQuestId = source.readString();
        mQuestName = source.readString();
        mTotalStages = source.readInt();
    }

    public String getQuestName() {
        return mQuestName;
    }

    public int getTotalStages() {
        return mTotalStages;
    }

    public String getQuestId() { return mQuestId; }

    @Override
    public String toString() {
        return mQuestName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestInfo that = (QuestInfo) o;

        return mQuestId.equals(that.mQuestId);

    }

    @Override
    public int hashCode() {
        return mQuestId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuestId);
        dest.writeString(mQuestName);
        dest.writeInt(mTotalStages);
    }

    public static final Parcelable.Creator<QuestInfo> CREATOR =
            new Parcelable.Creator<QuestInfo>() {

                @Override
                public QuestInfo createFromParcel(Parcel source) {
                    return new QuestInfo(source);
                }

                @Override
                public QuestInfo[] newArray(int size) {
                    return new QuestInfo[size];
                }
            };

}
