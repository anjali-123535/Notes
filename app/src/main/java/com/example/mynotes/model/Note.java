/*
 *
 *   Created Anjali Parihar on 20/6/21 7:45 PM
 *   Copyright Ⓒ 2021. All rights reserved Ⓒ 2021 http://freefuninfo.com/
 *   Last modified: 24/10/20 1:53 PM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.example.mynotes.model;

public class Note {
   private String title,note,created,updated;
private Boolean mark;
    public Note(String title, String note, String created,String updated,Boolean bookmark) {
        this.title = title;
        this.note = note;
        this.created = created;
        mark=bookmark;
        this.updated=updated;
    }

    public Note() {
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Boolean getMark() {
        return mark;
    }

    public void setMark(Boolean mark) {
        this.mark = mark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
