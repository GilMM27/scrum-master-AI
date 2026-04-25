import { createContext, useState, useCallback, type ReactNode } from "react";
import { Alert, Snackbar } from "@mui/material";

interface Notification {
  message: string;
  severity: "success" | "error";
}

interface NotificationContextValue {
  showSuccess: (message: string) => void;
  showError: (message: string) => void;
}

export const NotificationContext = createContext<
  NotificationContextValue | undefined
>(undefined);

interface NotificationProviderProps {
  children: ReactNode;
}

export const NotificationProvider = ({
  children,
}: NotificationProviderProps) => {
  const [notification, setNotification] = useState<Notification | null>(null);

  const showSuccess = useCallback((message: string) => {
    setNotification({ message, severity: "success" });
  }, []);

  const showError = useCallback((message: string) => {
    setNotification({ message, severity: "error" });
  }, []);

  const handleClose = () => setNotification(null);

  return (
    <NotificationContext.Provider value={{ showSuccess, showError }}>
      {children}
      <Snackbar
        open={Boolean(notification)}
        autoHideDuration={notification?.severity === "success" ? 3500 : 4000}
        onClose={handleClose}
      >
        {notification ? (
          <Alert onClose={handleClose} severity={notification.severity}>
            {notification.message}
          </Alert>
        ) : undefined}
      </Snackbar>
    </NotificationContext.Provider>
  );
};
