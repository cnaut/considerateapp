from django import forms

class UserForm(forms.Form):
    name = forms.CharField()

class StatForm(forms.Form):
    type = forms.CharField()
    value = forms.CharField()
