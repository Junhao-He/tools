/**
 * Licensed to the Apache Software Foundation （ASF） under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * （the "License"）； you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sam.znv.lopq;

import java.util.BitSet;

/**
 * Created by Administrator on 2017/10/26.
 */
public class TypeConvertUtil {
    public static long intarrayToLong(int[] array) {
        BitSet bs = new BitSet(Long.SIZE);
        assert array.length == Long.SIZE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1)
                bs.set(i);
        }
        long[] bs_l = bs.toLongArray();
        if (bs_l.length < 1) {
            return 0;
        } else {
            return bs_l[0];
        }
    }

    public static short intarrayToShort(int[] array) {
        BitSet bs = new BitSet(Short.SIZE);
        assert array.length == Short.SIZE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1)
                bs.set(i);
        }
        long[] bs_l = bs.toLongArray();
        if (bs_l.length < 1) {
            return 0;
        } else {
            short result = (short) bs_l[0];
            return result;
        }

    }

    public static String intarrayToString(int[] array) {
        StringBuilder result = new StringBuilder(array.length);
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
        }
        return result.toString();
    }

    public static int longOf1(long n) {
        int count = 0;
        while (n != 0) {
            count++;
            n = n & (n - 1);
        }
        return count;
    }

    public static int intOf1(int n) {
        int count = 0;
        while (n != 0) {
            count++;
            n = n & (n - 1);
        }
        return count;
    }

}
