from django.db import models
from djangotoolbox.fields import ListField
from djangotoolbox.fields import EmbeddedModelField

class User(models.Model):
    phone_id = models.TextField(unique=True)
    name = models.TextField()
    joined_on = models.DateTimeField(auto_now_add=True, null=True)

class Stat(models.Model):
    user = models.TextField()
    type = models.TextField()
    value = models.TextField()
    time_recorded = models.TextField()
    timestamp = models.DateTimeField(auto_now_add=True, null=True)
