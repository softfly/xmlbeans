/*   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package scomp.derivation.restriction.detailed;

import xbean.scomp.derivation.attributeRestriction.AttrEltDocument;
import xbean.scomp.derivation.attributeRestriction.RestrictedAttrT;

import java.math.BigInteger;

import scomp.common.BaseCase;
import org.apache.xmlbeans.XmlString;

/**
 * @owner: ykadiysk
 * Date: Jul 23, 2004
 * Time: 11:52:12 AM
 */
public class AttributeRestriction extends BaseCase {
   /**
    * A should be positive
    * B should be there by default
    * @throws Throwable
    */
    public void testAttributeABC() throws Throwable {
        AttrEltDocument doc = AttrEltDocument.Factory.newInstance();
        RestrictedAttrT elt = doc.addNewAttrElt();
        elt.setA(new BigInteger("-3"));
        assertTrue(!doc.validate(validateOptions));
        showErrors();
        assertEquals("b", elt.getB());
       XmlString expected=XmlString.Factory.newInstance();
       expected.setStringValue("c2");
        assertEquals(expected, elt.xgetC());
    }

    public void testAttributeDEF() throws Throwable {
        AttrEltDocument doc = AttrEltDocument.Factory.newInstance();
        RestrictedAttrT elt = doc.addNewAttrElt();
        XmlString expected=XmlString.Factory.newInstance();
        expected.setStringValue("a");
        elt.xsetD(expected);
        assertEquals("a", elt.getD());
        assertTrue(!doc.validate(validateOptions));
        showErrors();
        elt.setD("d");
        elt.setE("e");
        elt.setF("foobar");
        try {
            assertTrue(doc.validate());
        }
        catch (Throwable t) {
            doc.validate(validateOptions);
            showErrors();
            throw t;
        }
    }
  /**
   * G is prohibited, X can appear even though not explicit in type
   * @throws Throwable
   */
    public void testAttributeGX() throws Throwable {
        AttrEltDocument doc = AttrEltDocument.Factory.newInstance();
        RestrictedAttrT elt = doc.addNewAttrElt();
        elt.setG("foobar");
        assertTrue(!doc.validate(validateOptions));
        showErrors();
        elt.setX("myval");
        elt.unsetG();
        elt.setF("foobar");
         try {
            assertTrue(doc.validate());
        }
        catch (Throwable t) {
            doc.validate(validateOptions);
            showErrors();
            throw t;
        }

    }
}
