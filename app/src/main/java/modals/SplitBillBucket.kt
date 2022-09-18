package modals

import android.os.Parcel
import android.os.Parcelable

class SplitBillBucket(
    var splitBillId: Int?, var splitBillName: String?, var splitBillImageIndex: Int?,
    var splitBillTemporaryDelete: Int?, var splitBillCreatedDate: String?
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(splitBillId)
        parcel.writeString(splitBillName)
        parcel.writeValue(splitBillImageIndex)
        parcel.writeValue(splitBillTemporaryDelete)
        parcel.writeString(splitBillCreatedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SplitBillBucket> {
        override fun createFromParcel(parcel: Parcel): SplitBillBucket {
            return SplitBillBucket(parcel)
        }

        override fun newArray(size: Int): Array<SplitBillBucket?> {
            return arrayOfNulls(size)
        }
    }


}