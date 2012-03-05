from django.db import models
from djangotoolbox.fields import ListField

# Create your models here.
class TextMessage(models.Model):
    text = models.TextField()
    sender = models.TextField()
    receiver = ListField()
    timestamp = models.DateTimeField(auto_now_add=True, null=True)
