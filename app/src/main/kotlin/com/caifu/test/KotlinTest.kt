package com.caifu.test

import android.util.Log
import com.google.common.collect.Range
import com.google.common.collect.TreeRangeSet

fun test() {
    rangeSetTest()
}

private fun rangeSetTest() {
    var rangeSet = TreeRangeSet.create<Int>()
    rangeSet.add(Range.closed(0, 10))
    rangeSet.add(Range.closed(11, 20))
    Log.d(Constants.TAG, "rangeSet 1:$rangeSet")

    rangeSet = TreeRangeSet.create<Int>()
    rangeSet.add(Range.closed(0, 10))
    rangeSet.add(Range.closed(10, 20))
    Log.d(Constants.TAG, "rangeSet 2:$rangeSet")
}