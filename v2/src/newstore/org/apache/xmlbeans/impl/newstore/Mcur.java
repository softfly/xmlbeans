/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 2003 The Apache Software Foundation.  All rights 
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer. 
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:  
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Apache" and "Apache Software Foundation" must 
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written 
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache 
*    XMLBeans", nor may "Apache" appear in their name, without prior 
*    written permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2000-2003 BEA Systems 
* Inc., <http://www.bea.com/>. For more information on the Apache Software
* Foundation, please see <http://www.apache.org/>.
*/

package org.apache.xmlbeans.impl.newstore;

import java.io.PrintStream;

import java.util.Iterator;

import org.apache.xmlbeans.impl.newstore.pub.store.Locale;
import org.apache.xmlbeans.impl.newstore.pub.store.Locale.LoadContext;
import org.apache.xmlbeans.impl.newstore.pub.store.Cur;
import org.apache.xmlbeans.impl.newstore.pub.store.Dom;
import org.apache.xmlbeans.impl.newstore.pub.store.Dom.CharNode;
import org.apache.xmlbeans.impl.newstore.pub.store.Dom.TextNode;

import org.apache.xmlbeans.impl.newstore.SaajImpl;

import org.apache.xmlbeans.impl.newstore.CharUtil;
import org.apache.xmlbeans.impl.newstore.DomImpl;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.DOMImplementation;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPFaultElement;

import javax.xml.transform.Source;

import javax.xml.namespace.QName;

public final class Mcur extends Cur
{
    protected Locale _locale ( )
    {
        return _locale;
    }
    
    protected void _dispose ( )
    {
        _locale = null;
    }
    
    protected int _kind ( )
    {
        assert isNormal();
        return _xobj == null ? NONE : _xobj.kind( _pos );
    }
        
    protected void _createElement ( QName name, QName parentName )
    {
        assert isNormal();
        
        Xobj xo = _locale.createElement( name, parentName );
        
        if (_xobj != null)
        {
            Mcur from = tempCur( xo, 0 );
            from._moveNode( this );
            from.release();
        }
        
        set( xo, 0 );
    }
    
    protected void _create ( int kind, QName name )
    {
        assert isNormal();
        
        Xobj xo = _locale.createXobj( kind, name );

        if (_xobj != null)
        {
            Mcur from = tempCur( xo, 0 );
            from._moveNode( this );
            from.release();
        }
        
        set( xo, 0 );
    }

    protected void _toEnd ( )
    {
        assert _xobj != null && isNormal() && _pos == 0;

        set( _xobj, _xobj.posEnd() );
    }
    
    protected boolean _toParent ( boolean raw )
    {
        assert isNormal() && _xobj != null;

        if (_pos >= 1 && _pos <= _xobj.posEnd())
        {
            set( _xobj, 0 );
            return true;
        }

        assert _pos == 0 || _xobj._parent != null;

        if (_xobj._parent != null)
        {
            set( _xobj._parent, 0 );
            return true;
        }
        
        if (raw || _xobj.isRoot())
            return false;

        Mcur r = (Mcur) _locale.tempCur();
        r.createRoot( ROOT );
        r.next();
        _moveNode( r );
        r.release();

        assert _xobj._parent != null;

        set( _xobj._parent, 0 );

        return true;
    }

    protected boolean _toNextSibling ( )
    {
        assert _xobj != null && isNormal() && _pos == 0;

        if (_xobj.isAttr())
        {
            if (_xobj._nextSibling != null && _xobj._nextSibling.isAttr())
            {
                set( _xobj._nextSibling, 0 );
                return true;
            }
        }
        else if (_xobj._nextSibling != null)
        {
            set( _xobj._nextSibling, 0 );
            return true;
        }

        return false;
    }

    protected final boolean _toFirstAttr ( )
    {
        assert _xobj != null && isNormal() && _pos == 0;

        if (_xobj._firstChild == null || !_xobj._firstChild.isAttr())
            return false;

        set( _xobj._firstChild, 0 );

        return true;
    }

    protected boolean _toFirstChild ( )
    {
        assert _xobj != null && isNormal() && _pos == 0;

        for ( Xobj x = _xobj._firstChild ; x != null ; x = x._nextSibling )
        {
            if (!x.isAttr())
            {
                set( x, 0 );
                return true;
            }
        }

        return false;
    }
    
    protected final boolean _toLastChild ( )
    {
        assert _xobj != null && isNormal() && _pos == 0;

        if (_xobj._lastChild == null || _xobj._lastChild.isAttr())
            return false;

        set( _xobj._lastChild, 0 );

        return true;
    }

    protected boolean _isSamePosition ( Cur cThat )
    {
        Mcur that = (Mcur) cThat;

        assert isNormal() && that.isNormal();

        return _xobj == that._xobj && _pos == that._pos;
    }

    protected boolean _isPositioned ( )
    {
        return _xobj != null;
    }
    
    protected Dom _getDom ( )
    {
        assert _xobj != null;
        
        return _xobj.getDom();
    }

    protected void _moveToCur ( Cur c )
    {
        if (c == null)
            set( null, -1 );
        else
        {
            Mcur m = (Mcur) c;
            set( m._xobj, m._pos );
        }
    }
    
    protected void _moveToDom ( Dom d )
    {
        assert d instanceof Xobj || d instanceof SoapPartDom;
        assert d.locale() == _locale;

        set( d instanceof Xobj ? (Xobj) d : ((SoapPartDom) d)._docXobj, 0 );
    }
    
    protected void _setName ( QName name )
    {
        assert isNormal() && _xobj != null && _pos == 0 && (_xobj.isElem() || _xobj.isAttr());
        assert name != null;
        _xobj._name = name;
        
        _locale._versionAll++;
        _locale._versionSansText++;
    }
    
    protected final QName _getName ( )
    {
        assert isNormal() && _xobj != null && (_pos == 0 || _pos == _xobj.posEnd());
        return _xobj._name;
    }

    protected boolean _next ( )
    {
        assert isNormal();

        Xobj x = _xobj;
        int  p = _pos;

        int pe = x.posEnd();

        if (p > pe)
            p = _xobj.posMax();
        else if (p == pe)
        {
            if (x.isRoot() || (x.isAttr() && (x._nextSibling == null || !x._nextSibling.isAttr())))
                return false;
            
            p = pe + 1;
        }
        else if (p > 0)
        {
            assert x._firstChild == null || !x._firstChild.isAttr();

            if (x._firstChild != null)
            {
                x = x._firstChild;
                p = 0;
            }
            else
                p = pe;
        }
        else
        {
            assert p == 0;
            
            p = 1;
            
            if (x._cchValue == 0)
            {
                if (x._firstChild != null)
                {
                    if (x._firstChild.isAttr())
                    {
                        Xobj a = x._firstChild;

                        while ( a._nextSibling != null && a._nextSibling.isAttr() )
                            a = a._nextSibling;

                        if (a._cchAfter > 0)
                        {
                            x = a;
                            p = a.posAfter();
                        }
                        else if (a._nextSibling != null)
                        {
                            x = a._nextSibling;
                            p = 0;
                        }
                    }
                    else
                    {
                        x = x._firstChild;
                        p = 0;
                    }
                }
            }
        }

        set( getNormal( x, p ), _posTemp );

        return true;
    }
    
    protected void _insertChars ( Object src, int off, int cch )
    {
        assert isNormal() && cch >= 0;

        if (cch > 0)
        {
            Xobj x = getDenormal();
            int  p = _posTemp;

            for ( Mcur e = x.getEmbedded() ; e != null ; e = (Mcur) e._next )
                if (e != this && e._pos >= p)
                    e._pos += cch;

            if (p >= x.posAfter())
            {
                x._srcAfter =
                    _locale._charUtil.insertChars(
                        p - x.posAfter(),
                        x._srcAfter, x._offAfter, x._cchAfter, src, off, cch );
                
                x._offAfter = _locale._charUtil._offSrc;
                x._cchAfter = _locale._charUtil._cchSrc;
            }
            else
            {
                x._srcValue =
                    _locale._charUtil.insertChars(
                        p - 1,
                        x._srcValue, x._offValue, x._cchValue, src, off, cch );
                
                x._offValue = _locale._charUtil._offSrc;
                x._cchValue = _locale._charUtil._cchSrc;
            }
            
            _locale._versionAll++;
        }
    }

    protected void _copyNode ( Cur cTo )
    {
        // TODO - make moveNode, moveChars, etc, deal with targeting different
        // masters -- may have to copy instead of move .....

        assert cTo != null;
        assert _xobj != null && _pos == 0;

        // How to copy between fcur and mcur???
        Mlocale tm = (Mlocale) cTo.locale();
        
        Xobj newParent = null;
        Xobj copy = null;
        Xobj xo = _xobj;
            
        walk: for ( ; ; )
        {
            Xobj newXo = tm.createXobj( xo.kind(), xo._name );

            newXo._srcValue = xo._srcValue;
            newXo._srcAfter = xo._srcAfter;
            newXo._offValue = xo._offValue;
            newXo._offAfter = xo._offAfter;
            newXo._cchValue = xo._cchValue;
            newXo._cchAfter = xo._cchAfter;

            newXo._charNodesValue = CharNode.copyNodes( xo._charNodesValue, newXo );
            newXo._charNodesAfter = CharNode.copyNodes( xo._charNodesAfter, newXo );

            if (newParent == null)
                copy = newXo;
            else
                newParent.appendXobj( newXo );

            if (xo._firstChild != null)
            {
                newParent = newXo;
                xo = xo._firstChild;
            }
            else if (xo._nextSibling == null)
            {
                do
                {
                    if (xo == _xobj)
                        break walk;
                    
                    xo = xo._parent;
                    newParent = newParent._parent;

                    if (xo == _xobj)
                        break walk;
                }
                while ( xo._nextSibling == null );
            }
            else
                xo = xo._nextSibling;
        }

        copy._srcAfter = null;
        copy._offAfter = 0;
        copy._cchAfter = 0;

        Mcur to = (Mcur) cTo;

        if (to._xobj == null)
            to.set( copy, 0 );
        else
        {
            // TODO - how to operate between mcur and fcur
            
            Mcur from = (Mcur) tm.tempCur();
            from._moveNode( to );
            from.release();
        }
    }

    protected void _moveNode ( Cur cTo )
    {
        Mcur to = (Mcur) cTo;
        
        assert _xobj != null && _pos == 0 && !_xobj.isRoot();
        assert to == null || (to.isNormal() && !_ancestorOf( to ));
        assert to == null || (to._pos != 0 || (!to.isRoot() && !to.isAttr()));

        if (_xobj.cchAfter() > 0)
        {
            Mcur fromChars = tempCur( _xobj, _xobj.posAfter() );
            fromChars._moveChars( this, _xobj.cchAfter() );
            fromChars.release();
        }

        assert _xobj.cchAfter() == 0;

        _xobj.removeXobj();

        if (to != null)
        {
            Object srcRight = null;
            int cchRight = to.cchRight();
            
            if (cchRight > 0)
            {
                srcRight = to._moveChars( null, cchRight );
                assert cchRight == to._cchSrc;
            }

            assert to._pos == 0 || to._pos == to._xobj.posEnd();

            if (to._pos == 0)
                to._xobj.insertXobj( _xobj );
            else
                to._xobj.appendXobj( _xobj );

            if (srcRight != null)
            {
                Mcur toChars = tempCur( _xobj, _xobj.posAfter() );
                toChars._insertChars( srcRight, to._offSrc, cchRight );
                toChars.release();
            }
        }

        // todo - make a callback to master to do this work in addition
        // to providing a notification thast a chnage will take place
        // .. will have to make the call earler. ...
        
        _locale._versionAll++;
        _locale._versionSansText++;
    }
    
    protected Object _moveChars ( Cur cTo, int cchMove )
    {
        Mcur to = (Mcur) cTo;
        
        assert _xobj != null && isNormal() && (to == null || to.isNormal());
        assert cchMove >= 0 && cchMove <= cchRight();

        if (cchMove == 0)
            return null;

        if (to == null)
        {
            for ( Mcur e = _xobj.getEmbedded() ; e != null ; e = (Mcur) e._next )
            {
                if (e != this && e._pos >= _pos && e._pos < _pos + cchMove)
                {
                    e = (Mcur) _locale.tempCur();
                    e.createRoot( ROOT );
                    e.next();
                    Object chars = _moveChars( e, cchMove );
                    e.release();
                    return chars;
                }
            }
        }
        else
        {
            int pe = _xobj.posEnd();
            
            if (_xobj == to._xobj && to._pos >= _pos && to._pos < _pos + cchMove)
            {
                Object src;

                if (_pos > pe)
                {
                    src = _xobj._srcAfter;
                    _offSrc = _xobj._offAfter + _pos - pe - 1;
                }
                else
                {
                    src = _xobj._srcValue;
                    _offSrc = _xobj._offValue + _pos - 1;
                }

                _pos += cchMove;
                
                _cchSrc = cchMove;
                
                return src;
            }

            if (_pos <= pe)
                to._insertChars( _xobj._srcValue, _xobj._offValue + _pos - 1, cchMove );
            else
                to._insertChars( _xobj._srcAfter, _xobj._offAfter + _pos - pe - 1, cchMove );
        }

        Object srcMoved;
        int    offMoved;
        
        int pe = _xobj.posEnd();
        
        if (_pos <= pe)
        {
            int i = _pos - 1;
            
            srcMoved = _xobj._srcValue;
            offMoved = _xobj._offValue + i;

            _xobj._srcValue =
                _locale._charUtil.removeChars(
                    i, cchMove,
                    _xobj._srcValue, _xobj._offValue, _xobj._cchValue );

            _xobj._offValue = _locale._charUtil._offSrc;
            _xobj._cchValue = _locale._charUtil._cchSrc;
        }
        else
        {
            int i = _pos - pe - 1;
            
            srcMoved = _xobj._srcAfter;
            offMoved = _xobj._offAfter + i;

            _xobj._srcAfter =
                _locale._charUtil.removeChars(
                    i, cchMove,
                    _xobj._srcAfter, _xobj._offAfter, _xobj._cchAfter );

            _xobj._offAfter = _locale._charUtil._offSrc;
            _xobj._cchAfter = _locale._charUtil._cchSrc;
        }
        
        for ( Mcur e = _xobj.getEmbedded() ; e != null ; e = (Mcur) e._next )
            if (e != this && e._pos >= _pos && e._pos < _pos + cchMove)
                e.set( to._xobj, to._pos + e._pos - _pos );

        // The case where I delete all value text, _pos will be at end of node,
        // need to normalize to the first child (if any)
        
        if (_pos == _xobj.posEnd() && _xobj._firstChild != null)
            set( getNormal( _xobj._firstChild, 0 ), _posTemp );
        else
            set( getNormal( _xobj, _pos ), _posTemp );

        _locale._versionAll++;

        _offSrc = offMoved;
        _cchSrc = cchMove;

        return srcMoved;
    }
    
    protected boolean _ancestorOf ( Cur cThat )
    {
        Mcur that = (Mcur) cThat;
        
        assert _xobj != null && that._xobj != null;
        assert isNormal() && that.isNormal() && _pos == 0;

        if (_xobj == that._xobj && that._pos >= 1 && that._pos <= _xobj.posEnd())
            return true;

        if (_xobj._firstChild == null)
            return false;

        for ( Xobj x = that._xobj ; x != null ; x = x._parent )
            if (x == _xobj)
                return true;

        return false;
    }
    
    protected String _getStringValue ( )
    {
        assert isNormal() && _xobj != null && _pos == 0;
        return _xobj.getString( 1, _xobj._cchValue );
    }

    protected Object _getChars ( int cch )
    {
        assert isNormal() && _xobj != null;

        return _xobj.getChars( _pos, cch, this );
    }
    
    protected String _getString ( int cch )
    {
        assert isNormal() && _xobj != null;
        
        return _xobj.getString( _pos, cch );
    }

    protected String _getValueString ( )
    {
        assert isNormal() && _xobj != null && _pos == 0;
        
        // TODO - make sure there are no children (ok for an element to have
        // attrs)

        return _xobj.getString( 1, _xobj._cchValue );
    }

    protected CharNode _getCharNodes ( )
    {
        Xobj x = getDenormal();
        int  p = _posTemp;

        assert p > x.posEnd() || p > 0;

        CharNode nodes;

        if (p > x.posEnd())
        {
            nodes = x._charNodesAfter =
                updateCharNodes( _locale, x, x._charNodesAfter, x._cchAfter );
        }
        else
        {
            nodes = x._charNodesValue =
                updateCharNodes( _locale, x, x._charNodesValue, x._cchValue );
        }

        return nodes;
    }
    
    protected void _setCharNodes ( CharNode nodes )
    {
        Xobj x = getDenormal();
        int  p = _posTemp;

        assert p > x.posEnd() || p > 0;

        if (p > x.posEnd())
            x._charNodesAfter = nodes;
        else
            x._charNodesValue = nodes;

        for ( ; nodes != null ; nodes = nodes._next )
            nodes._src = x;
    }

    protected void _moveToCharNode ( CharNode node )
    {
        assert node._src instanceof Dom;

        _moveToDom( (Dom) node._src );

        CharNode n;

        n = _xobj._charNodesValue =
            updateCharNodes( _locale, _xobj, _xobj._charNodesValue, _xobj._cchValue );
        
        for ( ; n != null ; n = n._next )
        {
            if (node == n)
            {
                set( getNormal( _xobj, n._off + 1 ), _posTemp );
                return;
            }
        }

        n = _xobj._charNodesAfter =
            updateCharNodes( _locale, _xobj, _xobj._charNodesAfter, _xobj._cchAfter );

        for ( ; n != null ; n = n._next )
        {
            if (node == n)
            {
                set( getNormal( _xobj, n._off + _xobj._cchValue + 2 ), _posTemp );
                return;
            }
        }

        assert false;
    }
    
    protected final void _setBookmark ( Class c, Object o )
    {
        // BUGBUG - setting _obj must be more controlled. ... I want to
        // be able to move pointers to differnet backends .... need to
        // know which curs to move ...
        
        assert isNormal();
        assert c != null;
        assert o == null || o.getClass() == c;

        for ( Mcur x = _xobj.getEmbeddedRaw() ; x != null ; x = (Mcur) x._next )
        {
            if (x._pos == _pos && x._obj != null && x._obj.getClass() == c)
            {
                if (o == null)
                    x.release();
                else
                    x._obj = o;

                return;
            }
        }

        Cur cur = _locale.permCur();

        cur.moveToCur( this );

        assert cur._obj == null;

        cur._obj = o;
    }
    
    protected final Object _getBookmark ( Class c )
    {
        assert isNormal();
        assert c != null;

        for ( Mcur x = _xobj.getEmbeddedRaw() ; x != null ; x = (Mcur) x._next )
            if (x._pos == _pos && x._obj != null && x._obj.getClass() == c)
                return x._obj;
        
        return null;
    }
    
    //
    //
    //
    
    private Mcur ( Mlocale m )
    {
        _locale = m;
        _pos = -1;
    }

    private Mcur tempCur ( Xobj x, int p )
    {
        assert x != null || p == -1;

        Mcur m = (Mcur) _locale.tempCur();

        if (x != null && p == x.posMax())
        {
            if (x._nextSibling != null)
            {
                x = x._nextSibling;
                p = 0;
            }
            else
            {
                x = x.ensureParent();
                p = x.posEnd();
            }
        }

        m.set( x, p );

        return m;
    }

    private int cchRight ( )
    {
        assert _xobj != null &&  isNormal();
        return _xobj.cchRight( _pos );
    }

    private boolean isNormal ( )
    {
        return _xobj == null ? _pos == -1 : _xobj.isNormal( _pos );
    }

    private Xobj getNormal ( Xobj x, int p )
    {
        if (p == x.posMax())
        {
            if (x._nextSibling != null)
            {
                x = x._nextSibling;
                p = 0;
            }
            else
            {
                x = x.ensureParent();
                p = x.posEnd();
            }
        }

        _posTemp = p;

        return x;
    }

    private Xobj getDenormal ( )
    {
        assert _xobj != null && isNormal();
        
        Xobj x = _xobj;
        int  p = _pos;

        if (p == 0)
        {
            if (x._prevSibling != null)
            {
                x = x._prevSibling;
                p = x.posMax();
            }
            else
            {
                x = x.ensureParent();
                p = x.posEnd();
            }
        }
        else if (p == x.posEnd())
        {
            if (x._lastChild != null)
            {
                x = x._lastChild;
                p = x.posMax();
            }
        }

        _posTemp = p;

        return x;
    }

    
    public static Locale newLocale ( )
    {
        return new Mlocale();
    }
    
    private void set ( Xobj x, int p )
    {
        assert _state != POOLED;
        assert _state == EMBEDDED || _state == UNEMBEDDED;
        assert _xobj != null || _state == UNEMBEDDED;

        if (_state == EMBEDDED && x != _xobj)
        {
            assert _curKind != PERM;
            
            _xobj._embedded = listRemove( _xobj._embedded );
            _locale._unembedded = listInsert( _locale._unembedded, UNEMBEDDED );
        }

        _xobj = x;
        _pos = p;

        if (_curKind == PERM && _state == UNEMBEDDED && _xobj != null)
        {
            _locale._unembedded = listRemove( _locale._unembedded );
            _xobj._embedded = listInsert( _xobj._embedded, EMBEDDED );
        }

        assert isNormal();
    }

    public static final class Mlocale extends Locale
    {
        protected Cur newCur ( )
        {
            return new Mcur( this );
        }
        
        protected Xobj createXobj ( int kind, QName name )
        {
            assert name == null || (kind == ELEM || kind == ATTR || kind == XMLNS);
            assert (kind != ELEM && kind != ATTR && kind != XMLNS) || name != null;

            Xobj xo;

            switch ( kind )
            {
                case ROOT     : return new RootXobj         ( this       );
                case DOMDOC   : return     createDocument   (            );
                case DOMFRAG  : return new DocumentFragXobj ( this       );
                case ELEM     : return     createElement    ( name, null );
                case XMLNS    : return new XmlnsXobj        ( this, name );
                case ATTR     : return new AttrXobj         ( this, name );
                case COMMENT  : return     createComment    (            );
                case PROCINST : return new ProcInstXobj     ( this, name );

                default : throw new RuntimeException( "Unexpected kind" );
            }
        }

        protected LoadContext newLoadContext ( )
        {
            return new MLoadContext( this );
        }

        private Xobj createDocument ( )
        {
//            assert _ownerDoc == null;

            Xobj xo;

            if (_saaj == null)
                xo = new DocumentXobj( this );
            else
                xo = new SoapPartDocXobj( this );
            
            _ownerDoc = xo.getDom();

            return xo;
        }
        
        private ElementXobj createElement ( QName name, QName parentName )
        {
            if (_saaj != null)
            {
                Class c = _saaj.identifyElement( name, parentName );

                if (c == SOAPElement.class)       return new SoapElementXobj       ( this, name );
                if (c == SOAPBody.class)          return new SoapBodyXobj          ( this, name );
                if (c == SOAPBodyElement.class)   return new SoapBodyElementXobj   ( this, name );
                if (c == SOAPEnvelope.class)      return new SoapEnvelopeXobj      ( this, name );
                if (c == SOAPHeader.class)        return new SoapHeaderXobj        ( this, name );
                if (c == SOAPHeaderElement.class) return new SoapHeaderElementXobj ( this, name );
                if (c == SOAPFaultElement.class)  return new SoapFaultElementXobj  ( this, name );
                if (c == Detail.class)            return new DetailXobj            ( this, name );
                if (c == DetailEntry.class)       return new DetailEntryXobj       ( this, name );
                if (c == SOAPFault.class)         return new SoapFaultXobj         ( this, name );
                
                if (c != null)
                    throw new IllegalStateException();
            }
            
            return new ElementXobj( this, name );
        }

        private CommentXobj createComment ( )
        {
            return _saaj == null ? new CommentXobj( this ) : new SaajCommentXobj( this );
        }
        
        private CharUtil _charUtil = CharUtil.getThreadLocalCharUtil();
    }

    private static final class MLoadContext extends LoadContext
    {
        MLoadContext ( Mlocale m )
        {
            _locale = m;
            _frontier = _locale.createDocument();
            _after = false;
            
            _locale._versionAll++;
            _locale._versionSansText++;
        }

        protected void start ( Xobj xo )
        {
            assert _frontier != null;
            assert !_after || _frontier._parent != null;

            if (_after)
            {
                _frontier = _frontier._parent;
                _after = false;
            }

            _frontier.appendXobj( xo );
            _frontier = xo;
        }
        
        protected void end ( )
        {
            assert _frontier != null;
            assert !_after || _frontier._parent != null;

            if (_after)
                _frontier = _frontier._parent;
            else
                _after = true;
        }
        
        protected void startElement ( QName name )
        {
            start( _locale.createElement( name, (_after ? _frontier._parent :_frontier)._name ) );
        }
        
        protected void endElement ( )
        {
            assert (_after ? _frontier._parent : _frontier).isElem();
            end();
        }
        
        protected void xmlns ( String prefix, String uri )
        {
            assert (_after ? _frontier._parent : _frontier).isContainer();
            start( new XmlnsXobj( _locale, _locale.makeQName( null, prefix ) ) );
            text( uri );
            end();
        }
        
        protected void attr ( String local, String uri, String value )
        {
            assert (_after ? _frontier._parent : _frontier).isContainer();
            start( new AttrXobj( _locale, _locale.makeQName( uri, local ) ) );
            text( value );
            end();
        }
        
        protected void procInst ( String target, String value )
        {
            start( new ProcInstXobj( _locale, _locale.makeQName( null, target ) ) );
            text( value );
            end();
        }
        
        protected void comment ( char[] buf, int off, int cch )
        {
            start( _locale.createComment() );
            text( buf, off, cch );
            end();
        }
        
        protected void text ( String s )
        {
            text( s, 0, s.length() );
        }
        
        protected void text ( Object src, int off, int cch )
        {
            if (cch <= 0)
                return;

            if (_after)
            {
                _frontier._srcAfter =
                    _locale._charUtil.saveChars(
                        src, off, cch,
                        _frontier._srcAfter, _frontier._offAfter, _frontier._cchAfter );

                _frontier._offAfter = _locale._charUtil._offSrc;
                _frontier._cchAfter = _locale._charUtil._cchSrc;
            }
            else
            {
                _frontier._srcValue =
                    _locale._charUtil.saveChars(
                        src, off, cch,
                        _frontier._srcValue, _frontier._offValue, _frontier._cchValue );

                _frontier._offValue = _locale._charUtil._offSrc;
                _frontier._cchValue = _locale._charUtil._cchSrc;
            }
        }
        
        protected void text ( char[] src, int off, int cch )
        {
            text( (Object) src, off, cch );
        }
        
        protected Cur finish ( )
        {
            if (_after)
                _frontier = _frontier._parent;

            assert _frontier != null && _frontier._parent == null;

            Mcur c = (Mcur) _locale.tempCur();

            c.set( _frontier, 0 );

            return c;
        }

        public void dump ( )
        {
            _frontier.dump();
        }
        
        private Mlocale _locale;
        private Xobj    _frontier;
        private boolean _after;
    }

    private abstract static class Xobj 
    {
        Xobj ( Mlocale m, int kind, int domType )
        {
            _locale = m;
            _bits = (domType << 8) + kind;
        }

        abstract Dom getDom ( );
        
        final int kind    ( ) { return _bits & 0xFF; }
        final int type    ( ) { return kind() & 0x7; }
        final int domType ( ) { return _bits >> 8;   }

        final boolean isRoot      ( ) { return type() == ROOT; }
        final boolean isAttr      ( ) { return type() == ATTR; }
        final boolean isElem      ( ) { return type() == ELEM; }
        final boolean isLeaf      ( ) { return type() == LEAF; }
        final boolean isContainer ( ) { return typeIsContainer( type() ); }

        final int cchValue ( ) { return _cchValue; }
        final int cchAfter ( ) { return _cchAfter; }
        
        final int posEnd   ( ) { return 1 + _cchValue; }
        final int posAfter ( ) { return 2 + _cchValue; }
        final int posMax   ( ) { return 2 + _cchValue + _cchAfter; }

        final boolean isNormal ( int p )
        {
            if (p < 0 || p > posMax())
                return false;

            if (isRoot())
                return p <= posEnd();

            if (!isAttr())
                return p < posMax();

            if (p <= posEnd())
                return true;

            if (_cchAfter == 0)
                return false;

            if (_nextSibling != null && _nextSibling.isAttr())
                return false;

            if (_parent == null || !(_parent.isRoot() || _parent.type() == ELEM))
                return false;

            return true;
        }

        final int kind ( int p )
        {
            assert isNormal( p );
            return p == 0 ? kind() : p == posEnd() ? - kind() : TEXT;
        }

        public Cur tempCur ( )
        {
            Mcur mx = (Mcur) _locale.tempCur();
            mx.set( this, 0 );
            return mx;
        }

        final int cchRight ( int p )
        {
            assert isNormal( p );
            return p == 0 ? 0 : p <= posEnd() ? posEnd() - p : posMax() - p;
        }

        Xobj ensureParent ( )
        {
            assert _parent != null || (!isRoot() && cchAfter() == 0);
            return _parent == null ? new RootXobj( _locale ).appendXobj( this ) : _parent;
        }

        Mcur getEmbeddedRaw ( )
        {
            return (Mcur) _embedded;
        }
        
        Mcur getEmbedded ( )
        {
            while ( _locale._unembedded != null )
            {
                Mcur m = (Mcur) _locale._unembedded;
                _locale._unembedded = m.listRemove( _locale._unembedded );
                m._xobj._embedded = m.listInsert( m._xobj._embedded, EMBEDDED );
            }

            return (Mcur) _embedded;
        }

        Xobj removeXobj ( )
        {
            if (_parent != null)
            {
                if (_parent._firstChild == this)
                    _parent._firstChild = _nextSibling;

                if (_parent._lastChild == this)
                    _parent._lastChild = _prevSibling;

                if (_prevSibling != null)
                    _prevSibling._nextSibling = _nextSibling;

                if (_nextSibling != null)
                    _nextSibling._prevSibling = _prevSibling;

                _parent = null;
                _prevSibling = null;
                _nextSibling = null;
            }

            return this;
        }

        Xobj appendXobj ( Xobj c )
        {
            assert _locale == c._locale;
            assert !c.isRoot();
            assert c._parent == null;
            assert c._prevSibling == null;
            assert c._nextSibling == null;
            assert _lastChild == null || _firstChild != null;

            c._parent = this;
            c._prevSibling = _lastChild;

            if (_lastChild == null)
                _firstChild = c;
            else
                _lastChild._nextSibling = c;

            _lastChild = c;

            return this;
        }

        Xobj insertXobj ( Xobj s )
        {
            assert _locale == s._locale;
            assert !s.isRoot() && !isRoot();
            assert s._parent == null;
            assert s._prevSibling == null;
            assert s._nextSibling == null;

            s._parent = _parent;
            s._prevSibling = _prevSibling;
            s._nextSibling = this;

            if (_prevSibling != null)
                _prevSibling._nextSibling = s;
            else
                _parent._firstChild = s;

            _prevSibling = s;

            return this;
        }

        void insertValueText ( int i, Object src, int off, int cch )
        {
            assert cch > 0;
            
            _srcValue =
                _locale._charUtil.insertChars( i, _srcValue, _offValue, _cchValue, src, off, cch );
                
            _offValue = _locale._charUtil._offSrc;
            _cchValue = _locale._charUtil._cchSrc;
        }
        
        void insertAfterText ( int i, Object src, int off, int cch )
        {
            assert cch > 0;

            _srcAfter =
                _locale._charUtil.insertChars( i, _srcAfter, _offAfter, _cchAfter, src, off, cch );

            _offAfter = _locale._charUtil._offSrc;
            _cchAfter = _locale._charUtil._cchSrc;
        }
        
        private static String strInsert (
            int pos, String s, int off, int cch, String i, int i_off, int i_cch )
        {
            return
                s.substring( off, off + pos ) +
                    i.substring( i_off, i_off + i_cch ) +
                        s.substring( off + pos, off + cch );
        }

        String getString ( int pos, int cch )
        {
            int cchRight = cchRight( pos );

            if (cch < 0 || cch > cchRight)
                cch = cchRight;

            int pe = posEnd();

            // TODO - save this string back into the xobj for use later
            if (pos > pe)
                return CharUtil.getString( _srcAfter, _offAfter + pos - pe - 1, cch );
            else
                return CharUtil.getString( _srcValue, _offValue + pos - 1, cch );
        }

        Object getChars ( int pos, int cch, Cur c )
        {
            int cchRight = cchRight( pos );

            if (cch < 0 || cch > cchRight)
                cch = cchRight;

            if (cch == 0)
            {
                c._offSrc = 0;
                c._cchSrc = 0;
                
                return null;
            }

            int pe = posEnd();

            Object src;

            if (pos > pe)
            {
                src = _srcAfter;
                c._offSrc = _offAfter + pos - pe - 1;
            }
            else
            {
                src = _srcValue;
                c._offSrc = _offValue + pos - 1;
            }
            
            c._cchSrc = cch;
            
            return src;
        }
        
        void dump ( )
        {
            dump( System.out );
        }

        void dump ( PrintStream o )
        {
            Mcur.dump( o, this );
         }

        //        
        //        
        //
        
        private int _bits;
        
        Mlocale _locale;
        QName   _name;
        Cur     _embedded;

        Xobj _parent;
        Xobj _nextSibling;
        Xobj _prevSibling;
        Xobj _firstChild;
        Xobj _lastChild;

        // TODO - consider putting all but cch'es in ptr off this node
        Object _srcValue, _srcAfter;
        int    _offValue, _offAfter;
        int    _cchValue, _cchAfter;

        // TODO - put this in a ptr off this node
        CharNode _charNodesValue;
        CharNode _charNodesAfter;
    }

    private static class RootXobj extends Xobj
    {
        RootXobj ( Mlocale m ) { super( m, ROOT, 0 ); }
        
        Dom getDom ( ) { throw new IllegalStateException(); }
    }
    
    private static class SoapPartDocXobj extends Xobj
    {
        SoapPartDocXobj ( Mlocale m )
        {
            super( m, DOMDOC, DomImpl.DOCUMENT );
            _soapPartDom = new SoapPartDom( this );
        }

        Dom getDom ( ) { return _soapPartDom; }
        
        SoapPartDom _soapPartDom;
    }
    
    private abstract static class NodeXobj extends Xobj implements Node, Dom, NodeList
    {
        NodeXobj ( Mlocale m, int kind, int type ) { super( m, kind, type ); }
        
        public Locale locale     ( ) { return _locale;   }
        public int    nodeType   ( ) { return domType(); }
        
//        public Dom    altParent  ( ) { return _altParent;                   }
        
        Dom getDom ( ) { return this; }

        public QName qName ( ) { return _name; }

        public void dump ( PrintStream o )
        {
            Mcur.dump( o, (Xobj) this );
        }

        public void dump ( )
        {
            dump( System.out );
        }
        
        String getQName ( )
        {
            String prefix = _name.getPrefix();

            if (prefix == null || prefix.length() == 0)
                return _name.getLocalPart();
            else
                return prefix + ":" + _name.getLocalPart();
        }

        public int getLength ( ) { return DomImpl._childNodes_getLength( this ); }
        public Node item ( int i ) { return DomImpl._childNodes_item( this, i ); }

        public Node appendChild ( Node newChild ) { return DomImpl._node_appendChild( this, newChild ); }
        public Node cloneNode ( boolean deep ) { return DomImpl._node_cloneNode( this, deep ); }
        public NamedNodeMap getAttributes ( ) { return null; }
        public NodeList getChildNodes ( ) { return this; }
        public Node getParentNode ( ) { return DomImpl._node_getParentNode( this ); }
        public Node removeChild ( Node oldChild ) { return DomImpl._node_removeChild( this, oldChild ); }
        public Node getFirstChild ( ) { return DomImpl._node_getFirstChild( this ); }
        public Node getLastChild ( ) { return DomImpl._node_getLastChild( this ); }
        public String getLocalName ( ) { return DomImpl._node_getLocalName( this ); }
        public String getNamespaceURI ( ) { return DomImpl._node_getNamespaceURI( this ); }
        public Node getNextSibling ( ) { return DomImpl._node_getNextSibling( this ); }
        public String getNodeName ( ) { return DomImpl._node_getNodeName( this ); }
        public short getNodeType ( ) { return DomImpl._node_getNodeType( this ); }
        public String getNodeValue ( ) { return DomImpl._node_getNodeValue( this ); }
        public Document getOwnerDocument ( ) { return DomImpl._node_getOwnerDocument( this ); }
        public String getPrefix ( ) { return DomImpl._node_getPrefix( this ); }
        public Node getPreviousSibling ( ) { return DomImpl._node_getPreviousSibling( this ); }
        public boolean hasAttributes ( ) { return DomImpl._node_hasAttributes( this ); }
        public boolean hasChildNodes ( ) { return DomImpl._node_hasChildNodes( this ); }
        public Node insertBefore ( Node newChild, Node refChild ) { return DomImpl._node_insertBefore( this, newChild, refChild ); }
        public boolean isSupported ( String feature, String version ) { return DomImpl._node_isSupported( this, feature, version ); }
        public void normalize ( ) { DomImpl._node_normalize( this ); }
        public Node replaceChild ( Node newChild, Node oldChild ) { return DomImpl._node_replaceChild( this, newChild, oldChild ); }
        public void setNodeValue ( String nodeValue ) { DomImpl._node_setNodeValue( this, nodeValue ); }
        public void setPrefix ( String prefix ) { DomImpl._node_setPrefix( this, prefix ); }
        
        Dom _altParent;
    }
    
    private static class DocumentXobj extends NodeXobj implements Document
    {
        DocumentXobj ( Mlocale m )
        {
            super( m, DOMDOC, DomImpl.DOCUMENT );
        }

        public String name ( ) { return "#document"; }
        
        public Attr createAttribute ( String name ) { return DomImpl._document_createAttribute( this, name ); }
        public Attr createAttributeNS ( String namespaceURI, String qualifiedName ) { return DomImpl._document_createAttributeNS( this, namespaceURI, qualifiedName ); }
        public CDATASection createCDATASection ( String data ) { return DomImpl._document_createCDATASection( this, data ); }
        public Comment createComment ( String data ) { return DomImpl._document_createComment( this, data ); }
        public DocumentFragment createDocumentFragment ( ) { return DomImpl._document_createDocumentFragment( this ); }
        public Element createElement ( String tagName ) { return DomImpl._document_createElement( this, tagName ); }
        public Element createElementNS ( String namespaceURI, String qualifiedName ) { return DomImpl._document_createElementNS( this, namespaceURI, qualifiedName ); }
        public EntityReference createEntityReference ( String name ) { return DomImpl._document_createEntityReference( this, name ); }
        public ProcessingInstruction createProcessingInstruction ( String target, String data ) { return DomImpl._document_createProcessingInstruction( this, target, data ); }
        public Text createTextNode ( String data ) { return DomImpl._document_createTextNode( this, data ); }
        public DocumentType getDoctype ( ) { return DomImpl._document_getDoctype( this ); }
        public Element getDocumentElement ( ) { return DomImpl._document_getDocumentElement( this ); }
        public Element getElementById ( String elementId ) { return DomImpl._document_getElementById( this, elementId ); }
        public NodeList getElementsByTagName ( String tagname ) { return DomImpl._document_getElementsByTagName( this, tagname ); }
        public NodeList getElementsByTagNameNS ( String namespaceURI, String localName ) { return DomImpl._document_getElementsByTagNameNS( this, namespaceURI, localName ); }
        public DOMImplementation getImplementation ( ) { return DomImpl._document_getImplementation( this ); }
        public Node importNode ( Node importedNode, boolean deep ) { return DomImpl._document_importNode( this, importedNode, deep ); }
    }
    
    private static class DocumentFragXobj extends NodeXobj implements DocumentFragment
    {
        DocumentFragXobj ( Mlocale m ) { super( m, DOMFRAG, DomImpl.DOCFRAG ); }

        public String name ( ) { return "#document-fragment"; }
    }

    private static class ElementAttributes implements NamedNodeMap
    {
        ElementAttributes ( ElementXobj elementXobj )
        {
            _elementXobj = elementXobj;
        }
        
        public int getLength ( ) { return DomImpl._attributes_getLength( _elementXobj ); }
        public Node getNamedItem ( String name ) { return DomImpl._attributes_getNamedItem ( _elementXobj, name ); }
        public Node getNamedItemNS ( String namespaceURI, String localName ) { return DomImpl._attributes_getNamedItemNS ( _elementXobj, namespaceURI, localName ); }
        public Node item ( int index ) { return DomImpl._attributes_item ( _elementXobj, index ); }
        public Node removeNamedItem ( String name ) { return DomImpl._attributes_removeNamedItem ( _elementXobj, name ); }
        public Node removeNamedItemNS ( String namespaceURI, String localName ) { return DomImpl._attributes_removeNamedItemNS ( _elementXobj, namespaceURI, localName ); }
        public Node setNamedItem ( Node arg ) { return DomImpl._attributes_setNamedItem ( _elementXobj, arg ); }
        public Node setNamedItemNS ( Node arg ) { return DomImpl._attributes_setNamedItemNS ( _elementXobj, arg ); }

        private ElementXobj _elementXobj;
    }
    
    private static class ElementXobj extends NodeXobj implements Element
    {
        ElementXobj ( Mlocale m, QName name ) { super( m, ELEM, DomImpl.ELEMENT ); _name = name; }
        
        public String name ( ) { return getQName(); }
    
        public NamedNodeMap getAttributes ( )
        {
            if (_attributes == null)
                _attributes = new ElementAttributes( this );
            
            return _attributes;
        }
        
        public String getAttribute ( String name ) { return DomImpl._element_getAttribute( this, name ); }
        public Attr getAttributeNode ( String name ) { return DomImpl._element_getAttributeNode( this, name ); }
        public Attr getAttributeNodeNS ( String namespaceURI, String localName ) { return DomImpl._element_getAttributeNodeNS( this, namespaceURI, localName ); }
        public String getAttributeNS ( String namespaceURI, String localName ) { return DomImpl._element_getAttributeNS( this, namespaceURI, localName ); }
        public NodeList getElementsByTagName ( String name ) { return DomImpl._element_getElementsByTagName( this, name ); }
        public NodeList getElementsByTagNameNS ( String namespaceURI, String localName ) { return DomImpl._element_getElementsByTagNameNS( this, namespaceURI, localName ); }
        public String getTagName ( ) { return DomImpl._element_getTagName( this ); }
        public boolean hasAttribute ( String name ) { return DomImpl._element_hasAttribute( this, name ); }
        public boolean hasAttributeNS ( String namespaceURI, String localName ) { return DomImpl._element_hasAttributeNS( this, namespaceURI, localName ); }
        public void removeAttribute ( String name ) { DomImpl._element_removeAttribute( this, name ); }
        public Attr removeAttributeNode ( Attr oldAttr ) { return DomImpl._element_removeAttributeNode( this, oldAttr ); }
        public void removeAttributeNS ( String namespaceURI, String localName ) { DomImpl._element_removeAttributeNS( this, namespaceURI, localName ); }
        public void setAttribute ( String name, String value ) { DomImpl._element_setAttribute( this, name, value ); }
        public Attr setAttributeNode ( Attr newAttr ) { return DomImpl._element_setAttributeNode( this, newAttr ); }
        public Attr setAttributeNodeNS ( Attr newAttr ) { return DomImpl._element_setAttributeNodeNS( this, newAttr ); }
        public void setAttributeNS ( String namespaceURI, String qualifiedName, String value ) { DomImpl._element_setAttributeNS( this, namespaceURI, qualifiedName, value ); }

        // TODO - move attrs to perm cur which can hold all sorts of stuff on
        // nodes to free up memory.
        
        private ElementAttributes _attributes;
    }
    
    private static class AttrXobj extends NodeXobj implements Attr
    {
        AttrXobj ( Mlocale m ) { super( m, ATTR, DomImpl.ATTR ); }
        AttrXobj ( Mlocale m, QName name ) { super( m, ATTR, DomImpl.ATTR ); _name = name; }
        AttrXobj ( Mlocale m, int kind, int type ) { super( m, kind, type ); }
        AttrXobj ( Mlocale m, int kind, int type, QName name ) { super( m, kind, type ); _name = name; }
        
        public String name ( ) { return getQName(); }
        
        public String getName ( ) { return DomImpl._attr_getName( this ); }
        public Element getOwnerElement ( ) { return DomImpl._attr_getOwnerElement( this ); }
        public boolean getSpecified ( ) { return DomImpl._attr_getSpecified( this ); }
        public String getValue ( ) { return DomImpl._attr_getValue( this ); }
        public void setValue ( String value ) { DomImpl._attr_setValue( this, value ); }
    }
    
    private static class XmlnsXobj extends AttrXobj
    {
        XmlnsXobj ( Mlocale m, QName name ) { super( m, XMLNS, DomImpl.ATTR, name ); }
        
        public String name ( )
        {
//            assert _name.getPrefix() == null  || _name.getPrefix().length() == 0;
            // TODO - move this to a more generic place .....
            return "xmlns:" + _name.getLocalPart();
        }
    }
    
    private static class ProcInstXobj extends NodeXobj implements ProcessingInstruction
    {
        ProcInstXobj ( Mlocale m, QName name ) { super( m, PROCINST, DomImpl.PROCINST ); _name = name; }
        
        public String name ( ) { return _name.getLocalPart(); }
        
        public String getData ( ) { return DomImpl._processingInstruction_getData( this ); }
        public String getTarget ( ) { return DomImpl._processingInstruction_getTarget( this ); }
        public void setData ( String data ) { DomImpl._processingInstruction_setData( this, data ); }
    }
    
    private static class CommentXobj extends NodeXobj implements Comment
    {
        CommentXobj ( Mlocale m ) { super( m, COMMENT, DomImpl.COMMENT ); }

        public NodeList getChildNodes ( ) { return DomImpl._emptyNodeList; }
        
        public String name ( ) { return "#comment"; }
        
        public void appendData ( String arg ) { DomImpl._characterData_appendData( this, arg ); }
        public void deleteData ( int offset, int count ) { DomImpl._characterData_deleteData( this, offset, count ); }
        public String getData ( ) { return DomImpl._characterData_getData( this ); }
        public int getLength ( ) { return DomImpl._characterData_getLength( this ); }
        public void insertData ( int offset, String arg ) { DomImpl._characterData_insertData( this, offset, arg ); }
        public void replaceData ( int offset, int count, String arg ) { DomImpl._characterData_replaceData( this, offset, count, arg ); }
        public void setData ( String data ) { DomImpl._characterData_setData( this, data ); }
        public String substringData ( int offset, int count ) { return DomImpl._characterData_substringData( this, offset, count ); }
    }

    private static class SaajCommentXobj extends CommentXobj implements javax.xml.soap.Text
    {
        SaajCommentXobj ( Mlocale m ) { super( m ); }
        
        public String name ( ) { return "#comment"; }
        
        public Text splitText ( int offset ) { throw new IllegalStateException(); }

        public boolean isComment ( ) { return true; }
        
        public void detachNode ( ) { SaajImpl._soapNode_detachNode( this ); }
        public void recycleNode ( ) { SaajImpl._soapNode_recycleNode( this ); }
        public String getValue ( ) { return SaajImpl._soapNode_getValue( this ); }
        public void setValue ( String value ) { SaajImpl._soapNode_setValue( this, value ); }
        public SOAPElement getParentElement ( ) { return SaajImpl._soapNode_getParentElement( this ); }
        public void setParentElement ( SOAPElement p ) { SaajImpl._soapNode_setParentElement( this, p ); }
        
        public void appendData ( String arg ) { DomImpl._characterData_appendData( this, arg ); }
        public void deleteData ( int offset, int count ) { DomImpl._characterData_deleteData( this, offset, count ); }
        public String getData ( ) { return DomImpl._characterData_getData( this ); }
        public int getLength ( ) { return DomImpl._characterData_getLength( this ); }
        public void insertData ( int offset, String arg ) { DomImpl._characterData_insertData( this, offset, arg ); }
        public void replaceData ( int offset, int count, String arg ) { DomImpl._characterData_replaceData( this, offset, count, arg ); }
        public void setData ( String data ) { DomImpl._characterData_setData( this, data ); }
        public String substringData ( int offset, int count ) { return DomImpl._characterData_substringData( this, offset, count ); }
    }

    //
    // SAAJ objects
    //

    private static class SoapPartDom extends SOAPPart implements Dom, Document, NodeList
    {
        SoapPartDom ( SoapPartDocXobj docXobj )
        {
            _docXobj = docXobj;
        }
        
        public int    nodeType ( ) { return DomImpl.DOCUMENT;   }
        public Locale locale   ( ) { return _docXobj._locale;   }
        public Cur    tempCur  ( ) { return _docXobj.tempCur(); }
        public QName  qName    ( ) { return _docXobj._name;     }
        
        public void dump ( ) { dump( System.out ); }
        public void dump ( PrintStream o ) { _docXobj.dump( o ); }

        public String name ( ) { return "#document"; }
        
        public Node appendChild ( Node newChild ) { return DomImpl._node_appendChild( this, newChild ); }
        public Node cloneNode ( boolean deep ) { return DomImpl._node_cloneNode( this, deep ); }
        public NamedNodeMap getAttributes ( ) { return null; }
        public NodeList getChildNodes ( ) { return this; }
        public Node getParentNode ( ) { return DomImpl._node_getParentNode( this ); }
        public Node removeChild ( Node oldChild ) { return DomImpl._node_removeChild( this, oldChild ); }
        public Node getFirstChild ( ) { return DomImpl._node_getFirstChild( this ); }
        public Node getLastChild ( ) { return DomImpl._node_getLastChild( this ); }
        public String getLocalName ( ) { return DomImpl._node_getLocalName( this ); }
        public String getNamespaceURI ( ) { return DomImpl._node_getNamespaceURI( this ); }
        public Node getNextSibling ( ) { return DomImpl._node_getNextSibling( this ); }
        public String getNodeName ( ) { return DomImpl._node_getNodeName( this ); }
        public short getNodeType ( ) { return DomImpl._node_getNodeType( this ); }
        public String getNodeValue ( ) { return DomImpl._node_getNodeValue( this ); }
        public Document getOwnerDocument ( ) { return DomImpl._node_getOwnerDocument( this ); }
        public String getPrefix ( ) { return DomImpl._node_getPrefix( this ); }
        public Node getPreviousSibling ( ) { return DomImpl._node_getPreviousSibling( this ); }
        public boolean hasAttributes ( ) { return DomImpl._node_hasAttributes( this ); }
        public boolean hasChildNodes ( ) { return DomImpl._node_hasChildNodes( this ); }
        public Node insertBefore ( Node newChild, Node refChild ) { return DomImpl._node_insertBefore( this, newChild, refChild ); }
        public boolean isSupported ( String feature, String version ) { return DomImpl._node_isSupported( this, feature, version ); }
        public void normalize ( ) { DomImpl._node_normalize( this ); }
        public Node replaceChild ( Node newChild, Node oldChild ) { return DomImpl._node_replaceChild( this, newChild, oldChild ); }
        public void setNodeValue ( String nodeValue ) { DomImpl._node_setNodeValue( this, nodeValue ); }
        public void setPrefix ( String prefix ) { DomImpl._node_setPrefix( this, prefix ); }
        
        public Attr createAttribute ( String name ) { return DomImpl._document_createAttribute( this, name ); }
        public Attr createAttributeNS ( String namespaceURI, String qualifiedName ) { return DomImpl._document_createAttributeNS( this, namespaceURI, qualifiedName ); }
        public CDATASection createCDATASection ( String data ) { return DomImpl._document_createCDATASection( this, data ); }
        public Comment createComment ( String data ) { return DomImpl._document_createComment( this, data ); }
        public DocumentFragment createDocumentFragment ( ) { return DomImpl._document_createDocumentFragment( this ); }
        public Element createElement ( String tagName ) { return DomImpl._document_createElement( this, tagName ); }
        public Element createElementNS ( String namespaceURI, String qualifiedName ) { return DomImpl._document_createElementNS( this, namespaceURI, qualifiedName ); }
        public EntityReference createEntityReference ( String name ) { return DomImpl._document_createEntityReference( this, name ); }
        public ProcessingInstruction createProcessingInstruction ( String target, String data ) { return DomImpl._document_createProcessingInstruction( this, target, data ); }
        public Text createTextNode ( String data ) { return DomImpl._document_createTextNode( this, data ); }
        public DocumentType getDoctype ( ) { return DomImpl._document_getDoctype( this ); }
        public Element getDocumentElement ( ) { return DomImpl._document_getDocumentElement( this ); }
        public Element getElementById ( String elementId ) { return DomImpl._document_getElementById( this, elementId ); }
        public NodeList getElementsByTagName ( String tagname ) { return DomImpl._document_getElementsByTagName( this, tagname ); }
        public NodeList getElementsByTagNameNS ( String namespaceURI, String localName ) { return DomImpl._document_getElementsByTagNameNS( this, namespaceURI, localName ); }
        public DOMImplementation getImplementation ( ) { return DomImpl._document_getImplementation( this ); }
        public Node importNode ( Node importedNode, boolean deep ) { return DomImpl._document_importNode( this, importedNode, deep ); }
        
        public int getLength ( ) { return DomImpl._childNodes_getLength( this ); }
        public Node item ( int i ) { return DomImpl._childNodes_item( this, i ); }

        public void removeAllMimeHeaders ( ) { SaajImpl.soapPart_removeAllMimeHeaders( this ); }
        public void removeMimeHeader ( String name ) { SaajImpl.soapPart_removeMimeHeader( this, name ); }
        public Iterator getAllMimeHeaders ( ) { return SaajImpl.soapPart_getAllMimeHeaders( this ); }
        public SOAPEnvelope getEnvelope ( ) { return SaajImpl.soapPart_getEnvelope( this ); }
        public Source getContent ( ) { return SaajImpl.soapPart_getContent( this ); }
        public void setContent ( Source source ) { SaajImpl.soapPart_setContent( this, source ); }
        public String[] getMimeHeader ( String name ) { return SaajImpl.soapPart_getMimeHeader( this, name ); }
        public void addMimeHeader ( String name, String value ) { SaajImpl.soapPart_addMimeHeader( this, name,value ); }
        public void setMimeHeader ( String name, String value ) { SaajImpl.soapPart_setMimeHeader( this, name, value ); }
        public Iterator getMatchingMimeHeaders ( String[] names ) { return SaajImpl.soapPart_getMatchingMimeHeaders( this, names ); }
        public Iterator getNonMatchingMimeHeaders ( String[] names ) { return SaajImpl.soapPart_getNonMatchingMimeHeaders( this, names ); }
    
        SoapPartDocXobj _docXobj;
    }

    private static class SoapElementXobj
        extends ElementXobj implements SOAPElement, javax.xml.soap.Node
    {
        SoapElementXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public void detachNode ( ) { SaajImpl._soapNode_detachNode( this ); }
        public void recycleNode ( ) { SaajImpl._soapNode_recycleNode( this ); }
        public String getValue ( ) { return SaajImpl._soapNode_getValue( this ); }
        public void setValue ( String value ) { SaajImpl._soapNode_setValue( this, value ); }
        public SOAPElement getParentElement ( ) { return SaajImpl._soapNode_getParentElement( this ); }
        public void setParentElement ( SOAPElement p ) { SaajImpl._soapNode_setParentElement( this, p ); }
        
        public void removeContents ( ) { SaajImpl._soapElement_removeContents( this ); }
        public String getEncodingStyle ( ) { return SaajImpl._soapElement_getEncodingStyle( this ); }
        public void setEncodingStyle ( String encodingStyle ) { SaajImpl._soapElement_setEncodingStyle( this, encodingStyle ); }
        public boolean removeNamespaceDeclaration ( String prefix ) { return SaajImpl._soapElement_removeNamespaceDeclaration( this, prefix ); }
        public Iterator getAllAttributes ( ) { return SaajImpl._soapElement_getAllAttributes( this ); }
        public Iterator getChildElements ( ) { return SaajImpl._soapElement_getChildElements( this ); }
        public Iterator getNamespacePrefixes ( ) { return SaajImpl._soapElement_getNamespacePrefixes( this ); }
        public SOAPElement addAttribute ( Name name, String value ) throws SOAPException { return SaajImpl._soapElement_addAttribute( this, name, value ); }
        public SOAPElement addChildElement ( SOAPElement oldChild ) throws SOAPException { return SaajImpl._soapElement_addChildElement( this, oldChild ); }
        public SOAPElement addChildElement ( Name name ) throws SOAPException { return SaajImpl._soapElement_addChildElement( this, name ); }
        public SOAPElement addChildElement ( String localName ) throws SOAPException { return SaajImpl._soapElement_addChildElement( this, localName ); }
        public SOAPElement addChildElement ( String localName, String prefix ) throws SOAPException { return SaajImpl._soapElement_addChildElement( this, localName, prefix ); }
        public SOAPElement addChildElement ( String localName, String prefix, String uri ) throws SOAPException { return SaajImpl._soapElement_addChildElement( this, localName, prefix, uri ); }
        public SOAPElement addNamespaceDeclaration ( String prefix, String uri ) { return SaajImpl._soapElement_addNamespaceDeclaration( this, prefix, uri ); }
        public SOAPElement addTextNode ( String data ) { return SaajImpl._soapElement_addTextNode( this, data ); }
        public String getAttributeValue ( Name name ) { return SaajImpl._soapElement_getAttributeValue( this, name ); }
        public Iterator getChildElements ( Name name ) { return SaajImpl._soapElement_getChildElements( this, name ); }
        public Name getElementName ( ) { return SaajImpl._soapElement_getElementName( this ); }
        public String getNamespaceURI ( String prefix ) { return SaajImpl._soapElement_getNamespaceURI( this, prefix ); }
        public Iterator getVisibleNamespacePrefixes ( ) { return SaajImpl._soapElement_getVisibleNamespacePrefixes( this ); }
        public boolean removeAttribute ( Name name ) { return SaajImpl._soapElement_removeAttribute( this, name ); }
    }
    
    private static class SoapEnvelopeXobj extends SoapElementXobj implements SOAPEnvelope
    {
        SoapEnvelopeXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public SOAPBody addBody ( ) throws SOAPException { return SaajImpl._soapEnvelope_addBody( this ); }
        public SOAPBody getBody ( ) throws SOAPException { return SaajImpl._soapEnvelope_getBody( this ); }
        public SOAPHeader getHeader ( ) throws SOAPException { return SaajImpl._soapEnvelope_getHeader( this ); }
        public SOAPHeader addHeader ( ) throws SOAPException { return SaajImpl._soapEnvelope_addHeader( this ); }
        public Name createName ( String localName ) { return SaajImpl._soapEnvelope_createName( this, localName ); }
        public Name createName ( String localName, String prefix, String namespaceURI ) { return SaajImpl._soapEnvelope_createName( this, localName, prefix, namespaceURI ); }
    }

    private static class SoapHeaderXobj extends SoapElementXobj implements SOAPHeader
    {
        SoapHeaderXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public Iterator examineAllHeaderElements ( ) { return SaajImpl.soapHeader_examineAllHeaderElements( this ); }
        public Iterator extractAllHeaderElements ( ) { return SaajImpl.soapHeader_extractAllHeaderElements( this ); }
        public Iterator examineHeaderElements ( String actor ) { return SaajImpl.soapHeader_examineHeaderElements( this, actor ); }
        public Iterator examineMustUnderstandHeaderElements ( String mustUnderstandString ) { return SaajImpl.soapHeader_examineMustUnderstandHeaderElements( this, mustUnderstandString ); }
        public Iterator extractHeaderElements ( String actor ) { return SaajImpl.soapHeader_extractHeaderElements( this, actor ); }
        public SOAPHeaderElement addHeaderElement ( Name name ) { return SaajImpl.soapHeader_addHeaderElement( this, name ); }
    }
    
    private static class SoapBodyXobj extends SoapElementXobj implements SOAPBody
    {
        SoapBodyXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public boolean hasFault ( ) { return SaajImpl.soapBody_hasFault( this ); }
        public SOAPFault addFault ( ) throws SOAPException { return SaajImpl.soapBody_addFault( this ); }
        public SOAPFault getFault ( ) { return SaajImpl.soapBody_getFault( this ); }
        public SOAPBodyElement addBodyElement ( Name name ) { return SaajImpl.soapBody_addBodyElement( this, name ); }
        public SOAPBodyElement addDocument ( Document document ) { return SaajImpl.soapBody_addDocument( this, document ); }
        public SOAPFault addFault ( Name name, String s ) throws SOAPException { return SaajImpl.soapBody_addFault( this, name, s ); }
        public SOAPFault addFault ( Name faultCode, String faultString, java.util.Locale locale ) throws SOAPException { return SaajImpl.soapBody_addFault( this, faultCode, faultString, locale ); }
    }
    
    private static class SoapBodyElementXobj extends SoapElementXobj implements SOAPBodyElement
    {
        SoapBodyElementXobj ( Mlocale m, QName name ) { super( m, name ); }
    }
    
    private static class SoapFaultXobj extends SoapBodyElementXobj implements SOAPFault
    {
        SoapFaultXobj ( Mlocale m, QName name ) { super( m, name ); }

        public void setFaultString ( String faultString ) { SaajImpl.soapFault_setFaultString( this, faultString ); }
        public void setFaultString ( String faultString, java.util.Locale locale ) { SaajImpl.soapFault_setFaultString( this, faultString, locale ); }
        public void setFaultCode ( Name faultCodeName ) throws SOAPException { SaajImpl.soapFault_setFaultCode( this, faultCodeName ); }
        public void setFaultActor ( String faultActorString ) { SaajImpl.soapFault_setFaultActor( this, faultActorString ); }
        public String getFaultActor ( ) { return SaajImpl.soapFault_getFaultActor( this ); }
        public String getFaultCode ( ) { return SaajImpl.soapFault_getFaultCode( this ); }
        public void setFaultCode ( String faultCode ) throws SOAPException { SaajImpl.soapFault_setFaultCode( this, faultCode ); }
        public java.util.Locale getFaultStringLocale ( ) { return SaajImpl.soapFault_getFaultStringLocale( this ); }
        public Name getFaultCodeAsName ( ) { return SaajImpl.soapFault_getFaultCodeAsName( this ); }
        public String getFaultString ( ) { return SaajImpl.soapFault_getFaultString( this ); }
        public Detail addDetail ( ) throws SOAPException { return SaajImpl.soapFault_addDetail( this ); }
        public Detail getDetail ( ) { return SaajImpl.soapFault_getDetail( this ); }
    }

    private static class SoapHeaderElementXobj extends SoapElementXobj implements SOAPHeaderElement
    {
        SoapHeaderElementXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public void setMustUnderstand ( boolean mustUnderstand ) { SaajImpl.soapHeaderElement_setMustUnderstand( this, mustUnderstand ); }
        public boolean getMustUnderstand ( ) { return SaajImpl.soapHeaderElement_getMustUnderstand( this ); }
        public void setActor ( String actor ) { SaajImpl.soapHeaderElement_setActor( this, actor ); }
        public String getActor ( ) { return SaajImpl.soapHeaderElement_getActor( this ); }
    }
    
    private static class DetailEntryXobj extends SoapElementXobj implements DetailEntry
    {
        DetailEntryXobj ( Mlocale m, QName name ) { super( m, name ); }
    }

    private static class SoapFaultElementXobj extends SoapElementXobj implements SOAPFaultElement
    {
        SoapFaultElementXobj ( Mlocale m, QName name ) { super( m, name ); }
    }
    
    private static class DetailXobj extends SoapFaultElementXobj implements Detail
    {
        DetailXobj ( Mlocale m, QName name ) { super( m, name ); }
        
        public DetailEntry addDetailEntry ( Name name ) { return SaajImpl.detail_addDetailEntry( this, name ); }
        public Iterator getDetailEntries ( ) { return SaajImpl.detail_getDetailEntries( this ); }
    }

    //
    //
    //

    public void dump ( )
    {
        dump( System.out );
    }
    
    public void dump ( PrintStream o )
    {
        if (_xobj == null)
        {
            o.println( "Unpositioned xptr" );
            return;
        }

        dump( o, _xobj );
    }

    private static void dumpXcur ( PrintStream o, String prefix, Mcur mx )
    {
        o.print( " " + prefix + "cur[" + mx._pos + "]" );
    }
    
    private static void dumpXcurs ( PrintStream o, Xobj xo )
    {
        for ( Cur x = xo._embedded ; x != null ; x = x._next )
            dumpXcur( o, "*", (Mcur) x );
        
        for ( Cur x = xo._locale._unembedded ; x != null ; x = x._next )
        {
            Mcur mx = (Mcur) x;
            
            if (mx._xobj == xo)
                dumpXcur( o, "", (Mcur) x );
        }
    }
    
    private static void dumpNodes ( PrintStream o, CharNode nodes )
    {
        for ( CharNode n = nodes ; n != null ; n = n._next )
            o.print( " " + (n instanceof TextNode ? "TEXT" : "CDATA") + "[" + n._cch + "]" );
    }
    
    private static void dumpXobj ( PrintStream o, Xobj xo, int level, Xobj ref )
    {
        if (xo == null)
            return;

        if (xo == ref)
            o.print( "* " );
        else
            o.print( "  " );
        
        for ( int i = 0 ; i < level ; i++ )
            o.print( "  " );

        o.print( kindName( xo.kind() ) );

        if (xo._name != null)
            o.print( " " + xo._name );

        if (xo._srcValue != null || xo._charNodesValue != null)
        {
            o.print( " Value( " );
            CharUtil.dumpChars( o, xo._srcValue, xo._offValue, xo._cchValue );
            dumpNodes( o, xo._charNodesValue );
            o.print( " )" );
        }

        if (xo._srcAfter != null || xo._charNodesAfter != null)
        {
            o.print( " After( " );
            CharUtil.dumpChars( o, xo._srcAfter, xo._offAfter, xo._cchAfter );
            dumpNodes( o, xo._charNodesAfter );
            o.print( " )" );
        }

        dumpXcurs( o, xo );

        o.println();

        for ( xo = xo._firstChild ; xo != null ; xo = xo._nextSibling )
            dumpXobj( o, xo, level + 1, ref );
    }
    
    public static void dump ( PrintStream o, Xobj xo )
    {
        Xobj ref = xo;
        
        while ( xo._parent != null )
            xo = xo._parent;

        dumpXobj( o, xo, 0, ref );

        o.println();
    }
    
    //
    //
    //
    
    private Mlocale _locale;
    
    private Xobj _xobj;
    private int  _pos;

    private int _posTemp;
}