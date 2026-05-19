import { useEffect, useMemo, useState } from "react";

type DialogState<T> = {
  form: T;
  original: T;
  errorMsg: string;
  submitting: boolean;
  confirmDiscardOpen: boolean;
};

const makeInitialState = <T>(buildInitial: () => T): DialogState<T> => {
  const initial = buildInitial();
  return {
    form: initial,
    original: initial,
    errorMsg: "",
    submitting: false,
    confirmDiscardOpen: false,
  };
};

/**
 * Generic hook that manages the shared state pattern common to all form
 * dialogs: form values, a snapshot for change-detection, error message,
 * submitting flag, and the discard-confirmation gate.
 *
 * @param buildInitial  Factory called on every reset to produce the initial
 *                      form state.  Capture the props you depend on inside
 *                      the arrow function and list them in `resetDeps`.
 * @param resetDeps     Same values you would put in the `useEffect` deps
 *                      array in the component (e.g. [open, sprint, mode]).
 * @param onClose       The dialog's onClose callback, forwarded to
 *                      `handleRequestClose`.
 */
export function useFormDialog<T>(
  buildInitial: () => T,
  resetDeps: unknown[],
  onClose: () => void,
) {
  const [state, setState] = useState<DialogState<T>>(() =>
    makeInitialState(buildInitial),
  );

  // Resets all dialog state whenever any of the caller-supplied trigger values
  // change.  A single setState call avoids cascading re-renders.
  // `buildInitial` is captured by the closure and excluded from the literal
  // deps intentionally — the caller provides explicit triggers via `resetDeps`.
  useEffect(() => {
    setState(makeInitialState(buildInitial)); // eslint-disable-line react-hooks/set-state-in-effect
  }, resetDeps); // eslint-disable-line react-hooks/exhaustive-deps

  const hasChanges = useMemo(
    () => JSON.stringify(state.form) !== JSON.stringify(state.original),
    [state.form, state.original],
  );

  const setField = <K extends keyof T>(key: K, value: T[K]) =>
    setState((prev) => ({ ...prev, form: { ...prev.form, [key]: value } }));

  const setErrorMsg = (msg: string) =>
    setState((prev) => ({ ...prev, errorMsg: msg }));

  const setSubmitting = (value: boolean) =>
    setState((prev) => ({ ...prev, submitting: value }));

  const setConfirmDiscardOpen = (value: boolean) =>
    setState((prev) => ({ ...prev, confirmDiscardOpen: value }));

  /** Resets form values and error to the last-saved snapshot. */
  const restore = () =>
    setState((prev) => ({ ...prev, form: prev.original, errorMsg: "" }));

  /** Guards close with a discard-confirmation dialog when there are changes. */
  const handleRequestClose = () => {
    if (state.submitting) return;
    if (hasChanges) {
      setState((prev) => ({ ...prev, confirmDiscardOpen: true }));
      return;
    }
    onClose();
  };

  return {
    form: state.form,
    setField,
    errorMsg: state.errorMsg,
    setErrorMsg,
    submitting: state.submitting,
    setSubmitting,
    hasChanges,
    confirmDiscardOpen: state.confirmDiscardOpen,
    setConfirmDiscardOpen,
    handleRequestClose,
    /** Read-only snapshot of form values at the time of last reset. */
    original: state.original,
    restore,
  };
}
