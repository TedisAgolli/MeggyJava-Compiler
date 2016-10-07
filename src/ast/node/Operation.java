/* This file was generated by SableCC (http://www.sablecc.org/). 
 * Then modified.
 */

package ast.node;

import ast.visitor.Visitor;

@SuppressWarnings("nls")
public final class Operation extends IExp
{
    private IExp _lExp_;
    private IExp _rExp_;

    public Operation(int _line_, int _pos_, IExp _lExp_, IExp _rExp_)
    {
        super(_line_, _pos_);

        setLExp(_lExp_);

        setRExp(_rExp_);

    }
    
    @Override
    public int getNumExpChildren() { return 2; }

    @Override
    public Object clone()
    {
        return new Operation(
                this.getLine(),
                this.getPos(),
                cloneNode(this._lExp_),
                cloneNode(this._rExp_));
    }

    public void accept(	Visitor v)
    {
        v.visitOperation(this);
    }

    public IExp getLExp()
    {
        return this._lExp_;
    }

    public void setLExp(IExp node)
    {
        if(this._lExp_ != null)
        {
            this._lExp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lExp_ = node;
    }

    public IExp getRExp()
    {
        return this._rExp_;
    }

    public void setRExp(IExp node)
    {
        if(this._rExp_ != null)
        {
            this._rExp_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rExp_ = node;
    }

    @Override
    void removeChild(Node child)
    {
        // Remove child
        if(this._lExp_ == child)
        {
            this._lExp_ = null;
            return;
        }

        if(this._rExp_ == child)
        {
            this._rExp_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(Node oldChild, Node newChild)
    {
        // Replace child
        if(this._lExp_ == oldChild)
        {
            setLExp((IExp) newChild);
            return;
        }

        if(this._rExp_ == oldChild)
        {
            setRExp((IExp) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
