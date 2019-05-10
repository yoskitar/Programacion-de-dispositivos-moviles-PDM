using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Button : MonoBehaviour {

    private Material mat;
    public Color defaultColor;
    public Color selectedColor;

	// Use this for initialization
	void Start () {
        mat = GetComponent <Renderer>().material;
	}
	
    void OnTouchDown()
    {
        mat.color = selectedColor;
    }

    void OnMouseOver()
    {
        mat.color = selectedColor;
    }

    void OnMouseExit()
    {
        mat.color = defaultColor;
    }

    void OnTouchUp()
    {
        mat.color = defaultColor;
    }

    void OnTouchStay()
    {
        mat.color = selectedColor;
    }

    void OnTouchExit()
    {
        mat.color = defaultColor;
    }
}
