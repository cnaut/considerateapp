from django.db import models
from djangotoolbox.fields import ListField
from djangotoolbox.fields import EmbeddedModelField

class User(models.Model):
    joined_on = models.DateTimeField(auto_now_add=True, null=True)
