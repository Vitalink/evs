package org.imec.ivlab.core;

import java.io.Serializable;
import java.time.LocalDate;

public class Period implements Serializable {

        private LocalDate start;
        private LocalDate end;

        public Period(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        public LocalDate getStart() {
            return start;
        }

        public void setStart(LocalDate start) {
            this.start = start;
        }

        public LocalDate getEnd() {
            return end;
        }

        public void setEnd(LocalDate end) {
            this.end = end;
        }

        public boolean hasStart() {
            return start != null;
        }

        public boolean hasEnd() {
            return end != null;
        }

        public boolean isValid() {
            if (!hasStart()) {
                return false;
            }
            if (hasEnd() && end.isBefore(start)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Period{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Period period = (Period) o;

        if (start != null ? !start.equals(period.start) : period.start != null) return false;
        return end != null ? end.equals(period.end) : period.end == null;

    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}