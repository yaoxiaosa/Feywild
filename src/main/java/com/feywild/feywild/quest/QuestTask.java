package com.feywild.feywild.quest;

import com.feywild.feywild.quest.task.TaskType;
import com.feywild.feywild.quest.task.TaskTypes;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class QuestTask {
    
    private final TaskType<Object, Object> task;
    private final Object element;
    public final int times;

    private QuestTask(TaskType<Object, Object> task, Object element, int times) {
        this.task = task;
        this.element = element;
        this.times = times <= 0 ? 1 : times;
        if (!this.task.element().isAssignableFrom(element.getClass())) {
            throw new IllegalStateException("Can't create quest task: element type mismatch");
        }
        if (!this.task.repeatable() && this.times != 1) {
            throw new IllegalStateException("Can't create quest task: can't repeat non-repeatable task type.");
        }
    }
    
    public static <T> QuestTask of(TaskType<T, ?> type, T element) {
        return of(type, element, 1);
    }
    
    public static <T> QuestTask of(TaskType<T, ?> type, T element, int times) {
        //noinspection unchecked
        return new QuestTask((TaskType<Object, Object>) type, element, times);
    }

    public boolean checkCompleted(ServerPlayerEntity player, TaskType<?, ?> type, Object match) {
        if (this.task == type && this.task.testType().isAssignableFrom(match.getClass())) {
            return this.task.checkCompleted(player, this.element, match);
        } else {
            return false;
        }
    }
    
    public Item icon() {
        return this.task.icon(this.element);
    }
    
    public JsonObject toJson() {
        JsonObject json = this.task.toJson(this.element);
        json.addProperty("id", TaskTypes.getId(this.task).toString());
        if (this.times != 1) {
            json.addProperty("times", this.times);
        }
        return json;
    }

    public static QuestTask fromJson(JsonObject json) {
        //noinspection unchecked
        TaskType<Object, Object> task = (TaskType<Object, Object>) TaskTypes.getType(new ResourceLocation(json.get("id").getAsString()));
        Object element = task.fromJson(json);
        int times = json.has("times") ? json.get("times").getAsInt() : 1;
        return new QuestTask(task, element, times);
    }
}
